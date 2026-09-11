package br.com.monitordenoticias.desktop
import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.WinReg
import java.io.File
object WindowsStartup{
 private const val KEY="Software\\Microsoft\\Windows\\CurrentVersion\\Run"; private const val NAME="MonitorDeNoticias"
 fun getStartWithWindows():Boolean=runCatching{System.getProperty("os.name").lowercase().contains("win")&&Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,KEY,NAME)}.getOrDefault(false)
 fun setStartWithWindows(enabled:Boolean,exe:File){if(!System.getProperty("os.name").lowercase().contains("win"))return;if(enabled)Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER,KEY,NAME,"\"${exe.absolutePath}\"") else if(Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER,KEY,NAME))Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER,KEY,NAME)}
}
