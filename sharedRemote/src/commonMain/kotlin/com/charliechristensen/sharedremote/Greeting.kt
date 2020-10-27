package com.charliechristensen.sharedremote

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}
