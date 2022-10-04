package com.mahmoudrh.roomxml.domain.utils

sealed class OrderBy(val orderType: OrderType) {
    class Title(orderType: OrderType) : OrderBy(orderType)
    class Date(orderType: OrderType) : OrderBy(orderType)

    fun copy(orderType: OrderType): OrderBy {
        return when (this) {
            is Title -> Title(orderType)
            is Date -> Date(orderType)
        }
    }

    companion object {
        const val DATE = 0
        const val TITLE = 1

        fun getOrderBy(orderType: Int, orderBy: Int): OrderBy {
            val ot: OrderType = when (orderType) {
                OrderType.ASCENDING -> {
                    OrderType.Ascending
                }
                OrderType.DESCENDING -> {
                    OrderType.Descending
                }
                else -> {
                    OrderType.Descending
                }
            }
            val ob: OrderBy = when (orderBy) {
                DATE -> {
                    Date(ot)
                }
                TITLE -> {
                    Title(ot)
                }
                else -> {
                    Date(ot)
                }
            }

            return ob
        }

        fun getOrderByInt(orderBy: OrderBy): Int {
            return when (orderBy) {
                is Date -> DATE
                is Title -> TITLE
            }
        }

        fun getOrderTypeInt(orderBy: OrderBy): Int {
            return when (orderBy.orderType) {
                OrderType.Ascending -> OrderType.ASCENDING
                OrderType.Descending -> OrderType.DESCENDING
            }
        }
    }
}
