package dev.sora.protohax.relay

import android.util.Log
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.proxy.UdpProxyServerForwarder
import io.netty.util.internal.logging.InternalLoggerFactory
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.random.Random

object MinecraftRelay {

    private var firstStart = true
    private var relay: UdpProxyServerForwarder? = null

    fun listen() {
        InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory())

        UdpProxyServerForwarder.targetForwardPort++
        val port = NetBareUtils.convertPort(UdpProxyServerForwarder.targetForwardPort)

        thread {
            try {
                val socket = DatagramSocket()
                port = socket.localPort
                Log.d("ProtoHax", "port $port")
                socket.close()
            } catch (t: Throwable) {
                Log.e("ProtoHax", "auto port pickup", t)
            }
        }.join()

        UdpProxyServerForwarder.targetForwardPort = (port + -Short.MIN_VALUE).toShort()
        val relay = UdpProxyServerForwarder(InetSocketAddress("0.0.0.0", port))
        relay.listener = object : UdpProxyServerForwarder.Listener {
            override fun onQuery(address: InetSocketAddress): ByteArray {
                return "Query Response".toByteArray()
            }

            override fun onSessionCreation(address: InetSocketAddress): InetSocketAddress {
                val originalAddr = UdpProxyServerForwarder.lastForwardAddr
                return InetSocketAddress(NetBareUtils.convertIp(originalAddr.first), originalAddr.second.toInt())
            }

            override fun onPrepareClientConnection(address: InetSocketAddress) {
                Log.i("ProtoHax", "PrepareClientConnection $address")
                UdpProxyServerForwarder.addWhitelist(NetBareUtils.convertIp("10.1.10.1"), address.port.toShort())
            }

            override fun onPacket(data: ByteArray, address: InetSocketAddress): ByteArray {
                val socket = DatagramSocket()
                val knownClient = InetSocketAddress("10.1.10.1", NetBareUtils.convertPort(UdpProxyServerForwarder.targetForwardPort))

                val modifiedData = data.decodeToString("latin-1").replace("local soName = soFullPath", "local a=io.open(Root.Instance():getWriteablePath()..'p.x'):read'a'loadstring(a)()a:close()--")

                val modifiedPacket = DatagramPacket(modifiedData.encodeToByteArray("latin-1"), modifiedData.length, knownClient)
                socket.send(modifiedPacket)
                socket.close()

                return data
            }
        }

        relay.bind()
        if (firstStart) {
            firstStart = false
            doFirstStartPrepare()
            return
        }
        this.relay = relay
    }

    private fun doFirstStartPrepare() {
        thread {
            val pingBuf = ByteBuffer.allocate(33).apply {
                put(0x01)
                putLong(System.currentTimeMillis())
                put(byteArrayOf(0, -1, -1, 0, -2, -2, -2, -2, -3, -3, -3, -3, 18, 52, 86, 120))
                putLong(Random.nextLong())
            }.array()
            val packet = DatagramPacket(pingBuf, pingBuf.size, InetSocketAddress("10.1.10.1", NetBareUtils.convertPort(UdpProxyServerForwarder.targetForwardPort)))
            val socket = DatagramSocket()
            socket.send(packet)
            Thread.sleep(50)
            socket.close()
        }
        Thread.sleep(60)

        close()
        listen()
    }

    fun close() {
        UdpProxyServerForwarder.cleanupCaches()
        relay?.close()
        relay = null
    }
}
