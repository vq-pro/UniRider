package quebec.virtualite.commons.android.rest

import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

abstract class BaseBackendClient
{
    companion object
    {
        var unmockedRequest: String? = null
    }

    fun <T> api(baseUrl: String, type: Class<T>): T
    {
        val gson = GsonBuilder()
            .setLenient()
            .create()

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        return retrofit.create(type)
    }

    fun <T> request(onSuccess: (T) -> Unit): Callback<T>
    {
        unmockedRequest = null

        return object : Callback<T>
        {
            override fun onResponse(call: Call<T>, response: Response<T>)
            {
                if (response.isSuccessful && response.body() != null)
                {
                    onSuccess.invoke(response.body()!!)
                }
                else
                {
                    println(response.errorBody())
                }
            }

            override fun onFailure(call: Call<T?>, t: Throwable)
            {
                if (unmockedRequest != null)
                    throw AssertionError("Unmocked REST request: " + unmockedRequest)

                t.printStackTrace()
            }
        }
    }
}
