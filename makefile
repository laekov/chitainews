default : apk rund

apk : 
	./gradlew build

rund :
	adb -d install app/build/outputs/apk/debug/app-debug.apk

rune :
	adb -e install app/build/outputs/apk/debug/app-debug.apk
