package com.mahmoudrh.roomxml.domain.utils

sealed class OrderType {
    object Ascending : OrderType()
    object Descending : OrderType()
    companion object {
        const val DESCENDING = 0
        const val ASCENDING = 1
    }
}
