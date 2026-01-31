package com.example.valheimherbalist.domain

data class Inventory(
    private val items: Map<String, Int>
) {
    fun count(itemId: String): Int =
        items[itemId] ?: 0

    fun add(itemId: String, amount: Int): Inventory {
        require(amount >= 0 ) {"amount must be positive"}

        val newCount = count(itemId) + amount
        return copy(items = items + (itemId to newCount))
    }

    fun remove(itemId: String, amount: Int): Inventory {
        require(amount >= 0) {" $amount must be positive"}

        val updatedCount = count(itemId) - amount
        require(updatedCount >= 0) {"not enough $itemId to remove $amount"}

        return copy(items = items + (itemId to updatedCount))

    }

    companion object {
        val EMPTY = Inventory(emptyMap())
    }
}