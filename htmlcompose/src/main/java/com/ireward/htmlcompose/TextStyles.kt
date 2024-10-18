package com.ireward.htmlcompose

import android.text.Layout
import android.text.style.AlignmentSpan
import android.text.style.ParagraphStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign

fun TextStyle.fromSpans(pstyles: Array<out ParagraphStyle>): TextStyle {
    return pstyles.fold(this) { acc, item ->
        acc + item.toTextStyle()
    }
}

private fun ParagraphStyle.toTextStyle(): TextStyle {
    return when(this) {
        is AlignmentSpan -> TextStyle(textAlign = alignment?.toTextAlign())
        else -> TextStyle.Default
    }
}

private fun Layout.Alignment.toTextAlign(): TextAlign {
    return when (this) {
        Layout.Alignment.ALIGN_NORMAL -> TextAlign.Start
        Layout.Alignment.ALIGN_OPPOSITE -> TextAlign.End
        Layout.Alignment.ALIGN_CENTER -> TextAlign.Center
    }
}
