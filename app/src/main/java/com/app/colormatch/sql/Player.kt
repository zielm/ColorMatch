package com.app.colormatch.sql

class Player {
    var login : String? = null
    var password : String? = null
    var result : Int = 0

    constructor() {}

    constructor(login:String, password:String) {
        this.login = login
        this.password = password
    }



}