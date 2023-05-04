package com.unionacy.satoshidalasi

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// a data class that represents the response from the API
data class SatoshiResponse(val dalasi: Double, val satoshi: Double)

// an interface that defines the API endpoints
interface CryptoAPI {
    @GET("https://api.coingecko.com/api/v3/")
    suspend fun dalasiToSatoshi(
        @Path("amount") amount: Double): SatoshiResponse

    @GET("api convert amount btc to gmd")
    suspend fun satoshiToDalasi(
        @Path("amount") amount: Double): SatoshiResponse
}

// a Retrofit instance and initialize the API interface
val retrofit = Retrofit.Builder()
    .baseUrl("https://coingecko.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val cryptoAPI = retrofit.create(CryptoAPI::class.java)

// a composable function that will display the conversion results
@Composable
fun ConversionResult(dalasi: Double, satoshi: Double) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "${ dalasi } GMD = ${ satoshi } satoshi",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = "${ satoshi } satoshi = ${ dalasi } GMD",
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(16.dp)
        )
    }
}

// a composable function that will handle the user input and make API calls
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen() {
    var dalasi by remember { mutableStateOf(" ") }
    var satoshi by remember { mutableStateOf(" ") }
    
    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = dalasi,
            onValueChange = { dalasi = it },
            label = { Text(text = "Dalasi") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
            value = satoshi,
            onValueChange = { satoshi = it },
            label = { Text(text = "Satoshi") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (dalasi.isNotEmpty()) {
    CoroutineScope(Dispatchers.IO).launch
                    val response = cryptoAPI.dalasiToSatoshi(dalasi.toDouble())
                    satoshi = response.satoshi.toString()
                } else if (satoshi.isNotEmpty()) {
    CoroutineScope(Dispatchers.IO).launch
                    val response = cryptoAPI.satoshiToDalasi(satoshi.toDouble())
                    dalasi = response.dalasi.toString()
                }
            },
        ) {
            Text(text = "Convert")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (dalasi.isNotEmpty() || satoshi.isNotEmpty()) {
            ConversionResult(dalasi.toDouble(), satoshi.toDouble())
        }
    }
}