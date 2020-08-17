package com.charliechristensen.remote.websocketv2

import com.tinder.scarlet.StreamAdapter
import java.lang.reflect.Type

class FlowStreamAdapterFactory: StreamAdapter.Factory {
    override fun create(type: Type): StreamAdapter<Any, Any> = FlowStreamAdapter()
}
