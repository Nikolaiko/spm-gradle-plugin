package gradle.multiplatform.spm.services.parsers

class PackageFileParser {
    fun parseFileContent(content: String): String {
        val noSpacesContent = content.filterNot { it.isWhitespace() }
        val packageNameIndex = noSpacesContent.indexOf(NAME_SUBSTRING)

        val firstIndex = noSpacesContent.indexOf('"', packageNameIndex + 1)
        val secondIndex = noSpacesContent.indexOf('"', firstIndex + 1)

        return noSpacesContent.substring(firstIndex + 1, secondIndex)
    }

    companion object {
        private const val NAME_SUBSTRING = "name:"
    }
}