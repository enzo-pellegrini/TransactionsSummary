# TransactionsSummary
Project app for the course IT-AND1 at Via University College

## Description
I want to create an app that can see the user's transaction from multiple bank account, and that can provide the list of all the expenditures and varius summaries and graphs (hopefully).

## Current layout idea
The app will be divided in three screens, accessed through the navbar.
This screens are: 
+ **Transactions** Home page of the app, contains a small summary of the amount spent in the current period and a list of the transactions
  + In a small container he can see the total amount for the current period (selectable by the user interactively (maybe scrolling horizonttaly) and a prediction on the total expenditure by the end of the period, if the data is enough (unlinkely I can achieve that)
  + In a long list all the transactions, with small details such as those normally found in banking apps. Clicking on a transaction shows the location on a map and the category (editable). The transaction can be also marked as periodic, if the user expects the transaction to be repeated again and again, setting an ending date.
+ **Stats** In this page the user can see graphs of the total amount spent over time, and sections for amount spent for each city, amount spent for each seller and maybe more.
+ **Settings** All the settings are managed in this page.

## Prioritized List of Requirements
- Having a list of transactions on the first page, clicking a transaction shows details about the transaction, with a map showing the seller's location
- Having some stats on the second page in the form of pie charts and line chart showing the expenditures over time
- Being able to mark some transactions as periodic or ignored for the purposes of graphs, and to change a transaction's category
- Being able to actually connect one or multiple bank accounts
- Making a prediction of the expenditure for the current period (day, week or month) using a simple machine learning model trained on the user's previous transactions data. The training should be done periodically on a server function or the device.


# What I actually made
+ The home page contains a list of the transactions with their category and amount. clicking on the transaction shows more data about the transaction
+ Stats shows a pie chart with the distribution of expenditures in categories, and a line graph with the expenditures for each bank for each day.
+ Settings: User can add account here, delete, and the share button doesn't do anything, although the backend architecture fully supports sharing by inserting another user id in a list

## Requirements
- Was able to show list of transactions, clicking on them brings out more data. The location data was not available to me.
- The second page does have stas
- Can't mark transactions as periodic or ignored
- can connect multiple bank accounts
- No predictions

# Architecture
The backend is firebase, with firestore, some firestore triggers, and some serveless functions that can be called by the app.
