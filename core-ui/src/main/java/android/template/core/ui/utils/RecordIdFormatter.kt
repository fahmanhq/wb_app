package android.template.core.ui.utils

object RecordIdFormatter {

    fun format(recordId: String): String {
        return recordId.uppercase().takeLast(12)
    }
}