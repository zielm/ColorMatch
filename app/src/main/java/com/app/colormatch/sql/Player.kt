package com.app.colormatch.sql

class Player {
    var login : String? = null
    var points : Int = 0

    constructor() {}

    constructor(login:String, result:Int) {
        this.login = login
        this.points = result
    }



}