import * as functions from "firebase-functions";

// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//   functions.logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });

import { Configuration, PlaidEnvironments, PlaidApi, LinkTokenCreateRequest, Products, ItemPublicTokenExchangeRequest, TransactionsGetRequest, ItemRemoveRequest } from "plaid";
import { firestore, initializeApp } from "firebase-admin";



// initializeApp(functions.config().firebase);
initializeApp();

const db = firestore();

const configuration = new Configuration({
    basePath: PlaidEnvironments[functions.config().plaid.environment],
    baseOptions: {
        headers: {
            'PLAID-CLIENT-ID': functions.config().plaid.client_id,
            'PLAID-SECRET': functions.config().plaid.secret,
            'Plaid-Version': '2020-09-14',
        }
    }
})

const client = new PlaidApi(configuration);



export const produceLinkToken = functions.https.onCall(async (data, context) => {
    if (context.auth === undefined) {
        throw new functions.https.HttpsError("permission-denied", "Anonymous user not allowed");
    }

    let configs: LinkTokenCreateRequest = {
        user: {
            client_user_id: context.auth!!.uid,
        },
        client_name: 'Plaid quickstart',
        products: ["transactions" as Products],
        country_codes: functions.config().plaid.country_codes.split(","),
        language: 'en'
    }

    try {
        let res = await client.linkTokenCreate(configs);
        return res.data;
    } catch (error) {
        throw new functions.https.HttpsError("internal", "Failed calling backend", error);
    }
})



export const savePublicToken = functions.https.onCall(async (data, context) => {
    if (context.auth === undefined) {
        throw new functions.https.HttpsError("permission-denied", "Anonymous user not allowed");
    }

    const configs: ItemPublicTokenExchangeRequest = {
        public_token: data.public_token
    }
    try {
        const response = await client.itemPublicTokenExchange(configs);
        const accessToken = response.data.access_token;
        const itemId = response.data.item_id;
        const uid = context.auth!!.uid;
        const institutionName = data.institution_name;
        const institutionId = data.institution_id;
        
        const batch = db.batch();

        const secureItemRef = db.collection('secure_items').doc(itemId);
        batch.create(secureItemRef, {
            access_token: accessToken,
            account_ids: data.account_ids // Better save it, you never know
        });

        const itemRef = db.collection('items').doc(itemId);
        batch.create(itemRef, {
            institution_name: institutionName,
            institution_id: institutionId,
            item_id: itemId,
            owner: uid,
            read_access: [uid]
        });

        await batch.commit();
    } catch (err) {
        throw new functions.https.HttpsError('internal', "So much can have gone wrong", err);
    }
})


// TODO: Item deletion handling
export const deleteItem = functions.firestore
.document("items/{itemId}")
.onUpdate(async (change, context) => {
    const newValue = change.after.data();

    if (!(newValue.read_access as Array<String>).includes(newValue.owner)) {
        const secureItemRef = db.collection('secure_items').doc(newValue.item_id);
        const itemData = await secureItemRef.get();

        const req: ItemRemoveRequest = {
            access_token: itemData.data()!!.access_token
        }
        client.itemRemove(req);
        db.collection('items').doc(newValue.item_id).delete()
        secureItemRef.delete()
    }
})


export const fetchTransactions = functions.firestore
.document("secure_items/{itemId}")
.onCreate(async (snap, context) => {
    const newValue = snap.data();

    const accessToken: string = newValue.access_token;
    const itemId: string = context.params.itemId


    const request: TransactionsGetRequest = {
        access_token: accessToken,
        start_date: '2018-01-01',
        end_date: new Date().toISOString().split('T')[0]
    }

    try {
        const categories = (await client.categoriesGet({})).data.categories.reduce((map, obj) => {
            map.set(obj.category_id, obj.hierarchy[0]);
            return map;
        }, new Map<string, string>())

        const response = await client.transactionsGet(request);
        let transactions = response.data.transactions;
        const total_transactions = response.data.total_transactions;

        while (transactions.length < total_transactions) {
            const paginatedRequest: TransactionsGetRequest = {
                access_token: accessToken,
                start_date: '2018-01-01',
                end_date: new Date().toISOString().split('T')[0],
                options: {
                    offset: transactions.length
                }
            }
            const paginatedResponse = await client.transactionsGet(paginatedRequest);
            transactions = transactions.concat(
                paginatedResponse.data.transactions
            );
        }

        let adaptedTransactions = transactions.map(transaction => {
            return {
                transaction_id: transaction.transaction_id,
                name: transaction.name,
                amount: transaction.amount,
                category: categories.get(transaction.category_id!!),
                date: new Date(transaction.date)
            }
        })


        db.collection('items')
            .doc(itemId)
            .update({
                transactions: adaptedTransactions
            })
    } catch (err) {
        throw new functions.https.HttpsError('internal', 'Failed retrieving transactions', err);
    }
})