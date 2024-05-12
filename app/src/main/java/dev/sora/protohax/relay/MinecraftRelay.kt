package dev.sora.protohax.relay

import android.util.Log
import com.github.megatronking.netbare.NetBare
import com.github.megatronking.netbare.NetBareUtils
import com.github.megatronking.netbare.proxy.UdpProxyServerForwarder
import io.netty.util.internal.logging.InternalLoggerFactory
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.util.concurrent.locks.ReentrantLock

object MinecraftRelay {

    private var firstStart = true
    private var relay: RelayServer? = null

    fun listen() {
        InternalLoggerFactory.setDefaultFactory(NettyLoggerFactory())

        // Start NetBare if it's not already running
        if (!NetBare.isRunning) {
            NetBare.start()
        }

        UdpProxyServerForwarder.targetForwardPort++
        val port = NetBareUtils.convertPort(UdpProxyServerForwarder.targetForwardPort)

        relay = RelayServer(InetSocketAddress("0.0.0.0", port))
        relay?.listener = object : RelayListener {
            override fun onQuery(address: InetSocketAddress): ByteArray {
                Log.i("MinecraftRelay", "QUERY")
                return "Example Protocol;Example Relay;Example Version;Example Info;Example Port;$port".toByteArray()
            }

            override fun onSessionCreation(session: ServerSession): InetSocketAddress {
                val originalAddr = UdpProxyServerForwarder.lastForwardAddr
                val targetAddress = InetSocketAddress(NetBareUtils.convertIp(originalAddr.address), originalAddr.port)
                Log.i("MinecraftRelay", "SessionCreation: $targetAddress")
                return targetAddress
            }

            override fun onPrepareClientConnection(address: InetSocketAddress) {
                Log.i("MinecraftRelay", "PrepareClientConnection $address")
                UdpProxyServerForwarder.addWhitelist(NetBareUtils.convertIp("10.1.10.1"), address.port.toShort())
            }

            override fun onSession(session: RelaySession) {
                session.listener = object : RelaySessionListener(session) {
                    override fun onPacketInbound(packet: Packet): Boolean {
                        if ("Hello Test" == packet.data) {
                            // Modify the packet data
                            packet.data = "Hello World"
                            return false
                        }
                        return super.onPacketInbound(packet)
                    }

                    override fun onPacketOutbound(packet: Packet): Boolean {
                        if ("Give Fly Permission" == packet.data) {
                            // Simulate granting fly permission
                            return false
                        }
                        return super.onPacketOutbound(packet)
                    }
                }
            }
        }

        relay?.bind()

        if (firstStart) {
            firstStart = false
            doFirstStartPrepare()
            return
        }
    }

    private fun doFirstStartPrepare() {
        // Simulate first start preparation
        Thread.sleep(60L)
        close()
        listen()
    }

    fun close() {
        // Stop NetBare if it's not already stopped
        if (NetBare.isRunning) {
            NetBare.stop()
        }

        relay?.close(true)
        relay = null
    }
}

// Define your own relay class
class RelayServer(address: InetSocketAddress) {
    var listener: RelayListener? = null

    fun bind() {
        // Simulate binding the relay
        Log.i("MinecraftRelay", "Relay bound to $address")
    }

    fun close(force: Boolean) {
        // Simulate closing the relay
        Log.i("MinecraftRelay", "Relay closed")
    }
}

// Define your own relay listener interface
interface RelayListener {
    fun onQuery(address: InetSocketAddress): ByteArray
    fun onSessionCreation(session: ServerSession): InetSocketAddress
    fun onPrepareClientConnection(address: InetSocketAddress) {}
    fun onSession(session: RelaySession) {}
}

// Define your own relay session class
class RelaySession {
    var listener: RelaySessionListener? = null
}

// Define your own relay session listener interface
interface RelaySessionListener {
    fun onPacketInbound(packet: Packet): Boolean
    fun onPacketOutbound(packet: Packet): Boolean
}

// Define your own packet class
class Packet(var data: String)

// No custom logger required
