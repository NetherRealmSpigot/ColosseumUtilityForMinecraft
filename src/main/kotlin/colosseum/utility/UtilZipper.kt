package colosseum.utility

import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Path
import java.util.logging.*
import java.util.zip.*

class UtilZipper(val plugin: JavaPlugin) {
    private fun getFileList(fileList: MutableList<String>, node: File) {
        // add file only
        if (node.isFile) {
            fileList.add(node.absoluteFile.name)
        }

        if (node.isDirectory) {
            val subNote = node.list()
            if (subNote != null) {
                for (filename in subNote) {
                    getFileList(fileList, File(node, filename))
                }
            }
        }
    }

    fun zipFolders(sourceFolder: Path, outputZipFile: File, subFolders: List<Path>, individualFiles: List<File>) {
        var zipOutputStream: ZipOutputStream? = null
        var fileOutputStream: FileOutputStream? = null
        var fileInputStream: FileInputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null

        val fileList: MutableList<String> = ArrayList()
        val buffer = ByteArray(2048)

        try {
            fileOutputStream = FileOutputStream(outputZipFile)
            bufferedOutputStream = BufferedOutputStream(fileOutputStream)
            zipOutputStream = ZipOutputStream(bufferedOutputStream)

            var entry: ZipEntry

            for (file in individualFiles) {
                fileList.add(file.name)
            }

            for (folder in subFolders) {
                getFileList(fileList, folder.toFile())
            }

            for (filename in fileList) {
                entry = ZipEntry(filename)
                zipOutputStream.putNextEntry(entry)

                fileInputStream = FileInputStream(sourceFolder.resolve(filename).toFile())

                var len: Int
                while ((fileInputStream.read(buffer).also { len = it }) > 0) {
                    zipOutputStream.write(buffer, 0, len)
                }

                fileInputStream.close()
            }

            zipOutputStream.flush()
            zipOutputStream.close()

            bufferedOutputStream.flush()
            bufferedOutputStream.close()
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Error while zipping files", e)

            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error while zipping files", e1)
                }
            }

            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error while zipping files", e1)
                }
            }

            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error while zipping files", e1)
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error while zipping files", e1)
                }
            }

            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error while zipping files", e1)
                }
            }
        }
    }

    fun unzipToDirectory(zipFile: File, outputDirectory: Path) {
        var fileInputStream: FileInputStream? = null
        var zipInputStream: ZipInputStream? = null
        var fileOutputStream: FileOutputStream? = null
        var bufferedOutputStream: BufferedOutputStream? = null
        var bufferedInputStream: BufferedInputStream? = null

        try {
            fileInputStream = FileInputStream(zipFile)
            bufferedInputStream = BufferedInputStream(fileInputStream)
            zipInputStream = ZipInputStream(bufferedInputStream)

            var entry: ZipEntry?

            while ((zipInputStream.nextEntry.also { entry = it }) != null) {
                var size: Int
                val buffer = ByteArray(2048)

                val file = outputDirectory.resolve(entry!!.name).toFile()

                if (file.isDirectory && !file.exists()) {
                    file.mkdirs()
                    continue
                }

                fileOutputStream = FileOutputStream(file)
                bufferedOutputStream = BufferedOutputStream(fileOutputStream, buffer.size)

                while ((zipInputStream.read(buffer, 0, buffer.size).also { size = it }) != -1) {
                    bufferedOutputStream.write(buffer, 0, size)
                }

                bufferedOutputStream.flush()
                bufferedOutputStream.close()
                fileOutputStream.flush()
                fileOutputStream.close()
            }

            zipInputStream.close()
            bufferedInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Error unzipping file", e)

            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error unzipping file", e1)
                }
            }

            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error unzipping file", e1)
                }
            }

            if (zipInputStream != null) {
                try {
                    zipInputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error unzipping file", e1)
                }
            }

            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error unzipping file", e1)
                }
            }

            if (bufferedOutputStream != null) {
                try {
                    bufferedOutputStream.close()
                } catch (e1: IOException) {
                    plugin.logger.log(Level.SEVERE, "Error unzipping file", e1)
                }
            }
        }
    }
}
