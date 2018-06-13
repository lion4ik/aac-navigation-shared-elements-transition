package com.example.android.codelabs.navigation.recycler_view

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by msc10 on 16/02/2017.
 */
class AnimalItem : Parcelable {

    var name: String
    var detail: String
    var imageUrl: String

    constructor(name: String, detail: String, imageUrl: String) {
        this.name = name
        this.detail = detail
        this.imageUrl = imageUrl
    }

    constructor(parcel: Parcel) {
        name = parcel.readString()
        detail = parcel.readString()
        imageUrl = parcel.readString()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(detail)
        dest.writeString(imageUrl)
    }

    companion object {

        @JvmField
        val CREATOR: Parcelable.Creator<AnimalItem> = object : Parcelable.Creator<AnimalItem> {
            override fun createFromParcel(`in`: Parcel): AnimalItem {
                return AnimalItem(`in`)
            }

            override fun newArray(size: Int): Array<AnimalItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}