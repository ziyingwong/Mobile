package com.example.mobile

class Object_VolleyResponse {
    lateinit var data: Object_Data
}

class Object_Data {
    lateinit var boards: Object_Board
}

class Object_Board {
    lateinit var total: Integer
    lateinit var items: Array<Object_Items>
}

class Object_Items {
    lateinit var id: String
    lateinit var name: String
}

