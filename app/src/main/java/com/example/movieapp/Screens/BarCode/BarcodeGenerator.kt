package com.example.movieapp.Screens.BarCode

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

@Composable
fun BarcodeGenerator(content: String, modifier: Modifier = Modifier) {
    val bitmap = remember(content) {
        generateBarcodeBitmap(content)
    }

    bitmap?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = "Generated Barcode",
            modifier = modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(8.dp)
        )
    }
}

fun generateBarcodeBitmap(content: String): Bitmap? {
    return try {
        val writer = MultiFormatWriter()
        val bitMatrix = writer.encode(
            content,
//            BarcodeFormat.CODE_128,
//            600,
//            300
//        )
            BarcodeFormat.QR_CODE, // use QR_CODE instead of CODE_128
            600,
            600 // make square for QR
        )

        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] =
                    if (bitMatrix[x, y]) Color.Black.toArgb() else Color(0xFFCD9AC4).toArgb()
            }
        }

        Bitmap.createBitmap(pixels, width, height, Bitmap.Config.ARGB_8888)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}