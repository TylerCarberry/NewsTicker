package com.example.newsticker.model

data class Ticker(
        var text: String = ""
) {
    fun getTextAtIndex(index: Int, length: Int): String {
        var convertedIndex = index
        if (convertedIndex < 0) {
            convertedIndex = 0
        }

        convertedIndex %= (text.length + 4)
        return ("    " + text + "    ").substring(convertedIndex, convertedIndex + length)
    }

}
