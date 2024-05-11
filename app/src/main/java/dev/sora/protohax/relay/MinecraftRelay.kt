package dev.sora.protohax.relay

import android.util.Log
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.proxy.UdpProxyServerForwarder
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import kotlin.concurrent.thread
import kotlin.random.Random

object LuaLoader {

    private var firstStart = true

    fun listen() {
        UdpProxyServerForwarder.targetForwardPort++
        val port = NetBareUtils.convertPort(UdpProxyServerForwarder.targetForwardPort)

        thread {
            val socket = DatagramSocket()
            socket.bind(InetSocketAddress("0.0.0.0", port))
            Log.i("LuaLoader", "Listening on port $port")

            while (true) {
                val buffer = ByteArray(1024)
                val packet = DatagramPacket(buffer, buffer.size)
                socket.receive(packet)

                val data = String(packet.data, 0, packet.length)
                if (data.contains("soName = soFullPath")) {
                    val modifiedData = data.replace("soName = soFullPath", "soName = \"io.open(Root.Instance():getWriteablePath()..'p.x'):read()\"")
                    Log.i("LuaLoader", "Modified data: $modifiedData")
                    socket.send(DatagramPacket(modifiedData.toByteArray(), modifiedData.length, packet.address))
                }
            }
        }

        if (this.firstStart) {
            this.firstStart = false
            doFirstStartPrepare()
            return
        }
    }

    private fun doFirstStartPrepare() {
        thread {
            val socket = DatagramSocket()
            val pingBuf = ByteBuffer.allocate(33).apply {
                put(0x01)
                putLong(System.currentTimeMillis())
                put(byteArrayOf(0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120))
                putLong(Random.Default.nextLong())
            }.array()
            val packet = DatagramPacket(pingBuf, pingBuf.size, InetSocketAddress("10.1.10.1", NetBareUtils.convertPort(UdpProxyServerForwarder.targetForwardPort)))
            socket.send(packet)
            socket.close()
        }
        Thread.sleep(60L)

        close()
        listen()
    }

    fun close() {
        UdpProxyServerForwarder.cleanupCaches()
    }
}
