package io.github.skydynamic.maiprober.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import javax.crypto.spec.SecretKeySpec
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class OauthTokenUtil {
    companion object {
        private val tokenStatuses = ConcurrentHashMap<String, Boolean>()

        fun generateRandomSecretKey(length: Int = 64): String {
            val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
            return (1..length)
                .map { allowedChars.random() }
                .joinToString("")
        }

        fun generateToken(userId: String, secretKey: String, expirationTimeSeconds: Long): String {
            val now = Date()
            val expiration = Date(now.time + expirationTimeSeconds * 1000)

            val bytes = Decoders.BASE64.decode(secretKey) as ByteArray
            val key = SecretKeySpec(bytes, SignatureAlgorithm.HS256.jcaName)

            val token = Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact()

            tokenStatuses[token] = false

            return token
        }

        fun validateToken(token: String, secretKey: String): Boolean {
            val bytes = Decoders.BASE64.decode(secretKey) as ByteArray
            val key = SecretKeySpec(bytes, SignatureAlgorithm.HS256.jcaName)

            if (tokenStatuses[token] == true) {
                return false
            }

            return try {
                val claims: Claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .body
                claims.expiration >= Date()
            } catch (e: Exception) {
                false
            } finally {
                tokenStatuses[token] = true
            }
        }
    }
}