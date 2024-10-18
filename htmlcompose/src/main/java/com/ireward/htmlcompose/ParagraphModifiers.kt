package com.ireward.htmlcompose

import android.text.style.BulletSpan
import android.text.style.ParagraphStyle
import android.text.style.QuoteSpan
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

fun Modifier.paragraph(spans: Array<out ParagraphStyle>, fontSize: TextUnit, contentColor: Color): Modifier {
    return spans.fold(this) { acc, item ->
        acc.paragraph(item, fontSize, contentColor)
    }
}

fun Modifier.paragraph(style: ParagraphStyle?, fontSize: TextUnit, contentColor: Color): Modifier {
    return when(style) {
        is QuoteSpan -> this.padding(16.dp)
        is BulletSpan -> this.drawBehind {
            val radius = 2.dp.toPx()
            drawCircle(
                contentColor,
                radius = radius,
                center = Offset(radius, fontSize.toPx()),
            )
        }.padding(start = 8.dp)
        else -> this
    }
}