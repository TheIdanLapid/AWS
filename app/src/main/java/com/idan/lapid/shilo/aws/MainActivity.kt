package com.idan.lapid.shilo.aws

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun initAmazonS3Client(
        endpoint: String,
        accessKey: String,
        secretKey: String
    ) =
        AmazonS3Client(
            BasicAWSCredentials(accessKey, secretKey)
        ).apply {
            setEndpoint(endpoint).apply {
                println("S3 endpoint is ${endpoint}")
            }
            setS3ClientOptions(
                S3ClientOptions.builder()
                    .setPathStyleAccess(true).build()
            )
        }

    fun downloadFileFromS3(
        s3Client: AmazonS3Client,
        bucketName: String,
        targetFilename: String,
        pathToDownload: String
    ) =
        s3Client
            .getObject(GetObjectRequest(bucketName, targetFilename))
            .objectContent
            .use { inStream ->
                File("$pathToDownload/$targetFilename")
                    .outputStream().buffered().use { outStream ->
                        inStream.buffered().transferTo(outStream)
                    }
            }.also {
                println("Downloading file ${targetFilename} from S3")
            }
}