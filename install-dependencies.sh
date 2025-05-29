
mvn install:install-file -Dfile=../../starsector-core/starfarer.api.jar -DgroupId=FractalWorks -DartifactId=starfarer.api -Dversion=local -Dpackaging=jar
mvn install:install-file -Dfile=../../starsector-core/lwjgl.jar -DgroupId=org.lwjgl -DartifactId=lwjgl -Dversion=local -Dpackaging=jar
mvn install:install-file -Dfile=../../starsector-core/lwjgl_util.jar -DgroupId=org.lwjgl -DartifactId=lwjgl.util -Dversion=local -Dpackaging=jar

mvn install:install-file -Dfile=../lw_lazylib/jars/LazyLib.jar -DgroupId=LazyWizard -DartifactId=LazyLib -Dversion=local -Dpackaging=jar
mvn install:install-file -Dfile=../lw_lazylib/jars/LazyLib-Kotlin.jar -DgroupId=LazyWizard -DartifactId=LazyLib-Kotlin -Dversion=local -Dpackaging=jar
