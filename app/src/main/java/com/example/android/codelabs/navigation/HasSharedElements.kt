package com.example.android.codelabs.navigation

import android.view.View

/**
 * @author Alexey Pushkarev on 05.06.2018.
 */
interface HasSharedElements {

    fun getSharedElements() : Map<String, View>
}