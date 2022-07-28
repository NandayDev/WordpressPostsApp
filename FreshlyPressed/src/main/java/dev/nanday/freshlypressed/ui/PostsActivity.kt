package dev.nanday.freshlypressed.ui

import android.os.Bundle
import android.text.Html
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import dev.nanday.freshlypressed.R
import org.koin.androidx.viewmodel.ext.android.viewModel

class PostsActivity : FragmentActivity() {

    private val viewModel by viewModel<PostListActivityViewModel>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val postsData = viewModel.postsData.observeAsState()
            MainContent(postsData.value)
        }

        viewModel.loadPosts()
    }

    @Preview
    @Composable
    fun Preview() {
        MainContent(null)
    }

    @Composable
    fun MainContent(postsData: List<PostData>?) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (postsData == null) {
                // Loading! //
                CircularProgressIndicator()
            } else {
                // Show me the posts //
                LazyColumn {
                    items(postsData) { postData ->
                        ItemList(postData)
                    }
                }
                Text(text = "Not loading!")
            }
        }
    }

    @Composable
    fun ItemList(postData: PostData) {
        Row(modifier = Modifier.padding(8.dp)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (postData.image == null) {
                    Row(
                        modifier = Modifier.width(100.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Image(
                        bitmap = postData.image.asImageBitmap(),
                        contentDescription = "Article image",
                        modifier = Modifier
                            .size(135.dp)
                        //.clip(CircleShape)
                        //.border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)

                    )
                }
            }
            Column(
                modifier = Modifier.padding(start = 10.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = sanitizeTextFromApi(postData.title),
                    color = MaterialTheme.colors.secondaryVariant,
                    style = MaterialTheme.typography.subtitle2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = sanitizeTextFromApi(postData.excerpt),
                    style = MaterialTheme.typography.body2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = buildAnnotatedString {
                    withStyle(style = MaterialTheme.typography.body2.toSpanStyle()) {
                        append(getString(R.string.author))
                        append(": ")
                        append(postData.authorName)
                    }
                })


                Row {
                    Text(text = buildAnnotatedString {
                        withStyle(style = MaterialTheme.typography.body2.toSpanStyle()) {
                            append(getString(R.string.subscribers_count))
                            append(": ")
                            if (postData.subscribersCount != null) {
                                append(postData.subscribersCount)
                            }
                        }
                    })
                    if (postData.subscribersCount == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(15.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }

    private fun sanitizeTextFromApi(text: String): String {
        return Html.fromHtml(text)
            .trim()
            .toString()
    }
}
