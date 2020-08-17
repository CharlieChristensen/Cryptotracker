package com.charliechristensen.remote.websocketv2

import com.tinder.scarlet.Stream
import com.tinder.scarlet.StreamAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow

class FlowStreamAdapter<T>: StreamAdapter<T, Flow<T>> where T : Any {
    override fun adapt(stream: Stream<T>): Flow<T> = stream.asFlow()
}
