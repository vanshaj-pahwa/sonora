package app.vanshajpahwa.sonora.expect

actual fun getDownloadFolderPath(): String = System.getProperty("user.home") + "/Downloads"