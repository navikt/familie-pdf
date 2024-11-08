package no.nav.familie.pdf.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.util.concurrent.TimeUnit

@Configuration
@EnableCaching
class CacheConfig {
    @Bean
    @Primary
    fun cacheManager(): CacheManager =
        object : ConcurrentMapCacheManager() {
            override fun createConcurrentMapCache(name: String): Cache {
                val concurrentMap =
                    Caffeine
                        .newBuilder()
                        .maximumSize(1000)
                        .expireAfterWrite(60, TimeUnit.MINUTES)
                        .recordStats()
                        .build<Any, Any>()
                        .asMap()
                return ConcurrentMapCache(name, concurrentMap, true)
            }
        }
}

/**
 * Forventer treff, skal ikke brukes hvis en cache inneholder nullverdi
 * this.getCache(cache) burde aldri kunne returnere null, då den lager en cache hvis den ikke finnes fra før
 */
fun <K : Any, T> CacheManager.getValue(
    cache: String,
    key: K,
    valueLoader: () -> T,
): T = this.getNullable(cache, key, valueLoader) ?: error("Finner ikke cache for cache=$cache key=$key")

/**
 * Kan inneholde
 * this.getCache(cache) burde aldri kunne returnere null, då den lager en cache hvis den ikke finnes fra før
 */
fun <K : Any, T> CacheManager.getNullable(
    cache: String,
    key: K,
    valueLoader: () -> T?,
): T? = (getCacheOrThrow(cache)).get(key, valueLoader)

fun CacheManager.getCacheOrThrow(cache: String) = this.getCache(cache) ?: error("Finner ikke cache=$cache")

/**
 * Henter tidligere cachet verdier, og henter ucachet verdier med [valueLoader]
 */
@Suppress("UNCHECKED_CAST", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
fun <T, R> CacheManager.getCachedOrLoad(
    cacheName: String,
    values: List<T>,
    valueLoader: (List<T>) -> Map<T, R>,
): Map<T, R> {
    val cache = this.getCacheOrThrow(cacheName)
    val previousValues: List<Pair<T, R?>> = values.distinct().map { it to cache.get(it)?.get() as R? }.toList()

    val cachedValues = previousValues.mapNotNull { if (it.second == null) null else it }.toMap() as Map<T, R>
    val valuesWithoutCache = previousValues.filter { it.second == null }.map { it.first }
    val loadedValues: Map<T, R> =
        valuesWithoutCache
            .takeIf { it.isNotEmpty() }
            ?.let { valueLoader(it) } ?: emptyMap()
    loadedValues.forEach { cache.put(it.key, it.value) }

    return cachedValues + loadedValues
}
