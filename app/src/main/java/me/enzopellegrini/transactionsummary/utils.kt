// Extension of LiveData built by Google to make sure a change triggers the observer just once
// (works with only observer)
/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package me.enzopellegrini.transactionsummary

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean


class SingleLiveEvent<T> : MutableLiveData<T>() {

    private val mPending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        if (hasActiveObservers()) {
            Log.w( "SINGLELIVEEVENT", "Multiple observers registered but only one will be notified of changes"
            )
        }


        super.observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                if (mPending.compareAndSet(true, false)) {
                    observer.onChanged(t)
                }
            }
        })
    }

    override fun setValue(value: T) {
        mPending.set(true)
        super.setValue(value)
    }
}

// Simple Double extension method built by me
fun Double.toDollars(): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
    numberFormat.maximumFractionDigits = 2
    return numberFormat.format(this)
}