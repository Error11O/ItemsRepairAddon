package dev.Error110.itemsRepairAddon.utils

class RegenerateException(message: String?, vararg args: String?) : RuntimeException(message) {
    val args: Array<out String?>

    init {
        this.args = args
    }
}