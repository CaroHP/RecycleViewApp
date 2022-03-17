package com.example.segundo_parcial

import android.content.Context
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.lang.Exception
import java.net.URL
import kotlin.properties.Delegates

class FeedEntry {
    var name: String = ""
    var artist: String = ""
    var releaseDate: String = ""
    var summary: String = ""
    var imageUrl: String = ""

    override fun toString(): String {
        return """
            name = $name
            artist = $artist
            releaseDate = $releaseDate
            summary = $summary
            imageUrl = $imageUrl
        """.trimIndent()
    }
}

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private val downloadData: DownloadData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.xmlRecyclerView)

        Log.d(TAG, "onCreate")
        val downloadData = DownloadData(this, recyclerView)
        downloadData.execute("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
        Log.d(TAG, "onCreate Done")
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadData?.cancel(true)
    }

    companion object {
        private class DownloadData(context: Context, recyclerView: RecyclerView): AsyncTask<String, Void, String>() {

            var localContext: Context by Delegates.notNull()
            var localRecyclerView: RecyclerView by Delegates.notNull()

            init {
                localContext = context
                localRecyclerView = recyclerView
            }

            private val TAG = "DownloadData"
            override fun doInBackground(vararg url: String?): String {
                Log.d(TAG, "onPostExecute ${url[0]}")

                val rssFeed = downloadXML(url[0])
                if (rssFeed.isEmpty()) {
                    Log.e(TAG, "doInBackground: failed")
                }
                return rssFeed
            }

            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.d(TAG, "onPostExecute")
                val parsedApplication = ParseApplication()
                parsedApplication.parse(result)
                val adapter: ApplicationsAdapter = ApplicationsAdapter(localContext, parsedApps.aplication)
                localRecyclerView.adapter = adapter
                localRecyclerView.layoutManager = LinearLayoutManager(localContext)
            }

            class ParseApplication {
                private val TAG = "ParseApplications"
                val applications = ArrayList<FeedEntry>()

                fun parse( xmlData: String ): Boolean {
                    var status = true
                    var tagInEntry = false
                    var textValue = ""

                    try {
                        val factory = XmlPullParserFactory.newInstance()
                        factory.isNamespaceAware = true
                        val pullParser = factory.newPullParser()
                        //https://developer.android.com/reference/org/xmlpull/v1/XmlPullParser

                        pullParser.setInput(xmlData.reader())
                        var eventType = pullParser.eventType
                        var currentRecord = FeedEntry()
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            val tagName = pullParser.name?.lowercase()
                            when (eventType) {
                                XmlPullParser.START_TAG -> {
                                    Log.d(TAG, "parse: Starting tag for: $tagName")
                                    if(tagName == "entry") {
                                        tagInEntry = true
                                    }
                                }
                                XmlPullParser.TEXT -> {
                                    textValue = pullParser.text
                                }
                                XmlPullParser.END_TAG -> {
                                    Log.d(TAG, "parse: Ending tag for: $tagName")
                                    if(tagInEntry) {
                                        when(tagName) {
                                            "entry" -> {
                                                applications.add(currentRecord)
                                                tagInEntry = false
                                                currentRecord = FeedEntry()
                                            }
                                            "name" -> currentRecord.name = textValue
                                            "artist" -> currentRecord.artist = textValue
                                            "summary" -> currentRecord.summary = textValue
                                            "releasedate" -> currentRecord.releaseDate = textValue
                                            "image" -> currentRecord.imageUrl = textValue
                                        }
                                    }
                                }
                            }
                            eventType = pullParser.next()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        status = false
                    }

                    return true
                }
            }

            private fun downloadXML(urlPath: String?): String {
                    return URL(urlPath).readText()

            }
        }
    }
}