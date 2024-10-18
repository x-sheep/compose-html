package com.ireward.htmlcompose

import android.text.style.ParagraphStyle
import android.text.style.QuoteSpan
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.paragraph(spans: Array<out ParagraphStyle>): Modifier {
    return spans.fold(this) { acc, item ->
        acc.paragraph(item)
    }
}

fun Modifier.paragraph(style: ParagraphStyle?): Modifier {
    return when(style) {
        is QuoteSpan -> this.padding(16.dp)
        else -> this
    }
}
