package com.ireward.htmlcompose

import android.text.Spanned
import android.text.style.*
import android.text.style.ParagraphStyle
import android.widget.TextView
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.core.text.getSpans

private const val URL_TAG = "url_tag"

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    linkClicked: ((String) -> Unit)? = null,
    fontSize: TextUnit = 14.sp,
    flags: Int = HtmlCompat.FROM_HTML_MODE_COMPACT,
    URLSpanStyle: SpanStyle = SpanStyle(
        color = linkTextColor(),
        textDecoration = TextDecoration.Underline
    ),
    customSpannedHandler: ((Spanned) -> AnnotatedString)? = null
) {
    Column(modifier, verticalArrangement = spacedBy(0.dp)) {
        val spans = remember(text) {
            HtmlCompat.fromHtml(text, flags).splitParagraphs()
        }

        spans.forEach { spanned ->
            val content = spanned.toAnnotated(fontSize, URLSpanStyle, customSpannedHandler)
            val pstyles = spanned.getSpans<ParagraphStyle>()

            if (linkClicked != null) {
                ClickableText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .paragraph(pstyles),
                    text = content,
                    style = style.fromSpans(pstyles),
                    softWrap = softWrap,
                    overflow = overflow,
                    onTextLayout = onTextLayout,
                    onClick = {
                        content
                            .getStringAnnotations(URL_TAG, it, it)
                            .firstOrNull()
                            ?.let { stringAnnotation -> linkClicked(stringAnnotation.item) }
                    }
                )
            } else {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .paragraph(pstyles),
                    text = content,
                    style = style.fromSpans(pstyles),
                    softWrap = softWrap,
                    overflow = overflow,
                    onTextLayout = onTextLayout
                )
            }
        }
    }
}

private fun Spanned.splitParagraphs(): List<Spanned> = buildList {
    val len = length
    val text = this@splitParagraphs
    var i = 0

    while(i < len) {
        val next = text.nextSpanTransition(i, len, ParagraphStyle::class.java)
        add(text.subSequence(i, next) as Spanned)
        i = next
    }
}

@Composable
private fun linkTextColor() = Color(
    TextView(LocalContext.current).linkTextColors.defaultColor
)

@Composable
private fun Spanned.toAnnotated(
    fontSize: TextUnit,
    URLSpanStyle: SpanStyle,
    customSpannedHandler: ((Spanned) -> AnnotatedString)? = null
) = buildAnnotatedString {
    val spanned = this@toAnnotated
    val spans = spanned.getSpans(0, spanned.length, Any::class.java)

    if (customSpannedHandler != null) {
        append(customSpannedHandler(spanned))
    } else {
        append(spanned.toString())
    }

    spans
        .filter { it !is BulletSpan }
        .forEach { span ->
            val start = spanned.getSpanStart(span)
            val end = spanned.getSpanEnd(span)
            when (span) {
                is RelativeSizeSpan -> span.spanStyle(fontSize)
                is StyleSpan -> span.spanStyle()
                is UnderlineSpan -> span.spanStyle()
                is ForegroundColorSpan -> span.spanStyle()
                is TypefaceSpan -> span.spanStyle()
                is StrikethroughSpan -> span.spanStyle()
                is SuperscriptSpan -> span.spanStyle()
                is SubscriptSpan -> span.spanStyle()
                is URLSpan -> {
                    addStringAnnotation(
                        tag = URL_TAG,
                        annotation = span.url,
                        start = start,
                        end = end
                    )
                    URLSpanStyle
                }
                else -> {
                    null
                }
            }?.let { spanStyle ->
                addStyle(spanStyle, start, end)
            }
        }
}