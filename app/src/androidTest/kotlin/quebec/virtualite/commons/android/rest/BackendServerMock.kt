package quebec.virtualite.commons.android.rest

import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.apache.http.HttpHeaders.AUTHORIZATION
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.collection.IsMapContaining.hasKey
import quebec.virtualite.commons.android.rest.HttpMethod.*
import quebec.virtualite.commons.android.rest.JsonUtils.serialize

class BackendServerMock
{
    companion object
    {
        var calledRequests = HashMap<String, RecordedRequest>()
        var mockedRequests = HashMap<String, Any>()
    }

    val server = MockWebServer()

    fun start(port: Int)
    {
        server.start(port)

        server.dispatcher = object : Dispatcher()
        {
            override fun dispatch(request: RecordedRequest): MockResponse
            {
                val service = service(request)

                calledRequests.put(service, request)

                val responseBody = mockedRequests.get(service)
                if (responseBody != null)
                {
                    return MockResponse().setBody(serialize(responseBody))
                }

                BaseBackendClient.unmockedRequest = service
                return MockResponse()
            }
        }
    }

    fun stop()
    {
        server.close()
        server.shutdown()
    }

    fun verifyGet(url: String, token: String? = null)
    {
        verify(GET, url, token)
    }

    fun verifyPost(url: String, token: String? = null)
    {
        verify(POST, url, token)
    }

    fun verifyPut(url: String, token: String? = null)
    {
        verify(PUT, url, token)
    }

    fun whenGet(url: String, token: String? = null): ThenReturn
    {
        return ThenReturn(service(GET, url, token))
    }

    fun whenPost(url: String, token: String? = null): ThenReturn
    {
        return ThenReturn(service(POST, url, token))
    }

    fun whenPut(url: String, token: String? = null): ThenReturn
    {
        return ThenReturn(service(PUT, url, token))
    }

    private fun service(request: RecordedRequest): String
    {
        val token = request.headers.get(AUTHORIZATION)
        return service(request.method!!, request.path!!, token)
    }

    private fun service(method: HttpMethod, url: String, token: String?): String
    {
        return service(method.name, url, token)
    }

    private fun service(method: String, url: String, token: String?): String
    {
        return when (token)
        {
            null -> "$method $url"
            else -> "$method $url [$token]"
        }
    }

    private fun verify(method: HttpMethod, url: String, token: String?)
    {
        val service = service(method, url, token)
        assertThat("Backend was never called with " + service, calledRequests, hasKey(service))
    }
}

class ThenReturn(val service: String)
{
    fun thenReturn(body: Any)
    {
        BackendServerMock.mockedRequests.put(service, body)
    }
}

private enum class HttpMethod
{
    GET,
    POST,
    PUT
}
