package com.rarnu.web

object JsRouting {
    private val listRouting = mutableListOf<Route>()
    private fun routingExists(route: Route) = listRouting.filter { it.routing == route.routing}.isNotEmpty()
    private fun routingExists(route: String) = listRouting.filter { it.routing == route}.isNotEmpty()
    fun registerRouting(route: Route) { if (!routingExists(route)) { listRouting.add(route) } }
    fun registerRouting(routing: String, exec: (Map<String, Any>?) -> Map<String, Any>?) {
        if (!JsRouting.routingExists(routing)) {
            val r = object : Route(routing) {
                override fun execute(param: Map<String, Any>?): Map<String, Any>? {
                    return exec(param)
                }
            }
            listRouting.add(r)
        }
    }
    fun removeRouting(route: Route) = listRouting.removeIf { it.routing == route.routing }
    fun removeRouting(route: String) = listRouting.removeIf { it.routing == route }
    fun find(route: String) = listRouting.find { it.routing == route }
    abstract class Route(val routing: String) { abstract fun execute(param: Map<String, Any>?): Map<String, Any>? }
}