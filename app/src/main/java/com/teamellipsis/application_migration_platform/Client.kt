package com.teamellipsis.application_migration_platform

    import org.java_websocket.client.WebSocketClient
    import org.java_websocket.handshake.ServerHandshake
    import java.lang.Exception
    import java.net.URI


    class Client(serverURI: URI?) : WebSocketClient(serverURI) {
        override fun onClose(p0: Int, p1: String?, p2: Boolean) {

        }

        override fun onMessage(p0: String?) {

        }

        override fun onError(p0: Exception?) {

        }

        override fun onOpen(p0: ServerHandshake?) {

        }

    }
