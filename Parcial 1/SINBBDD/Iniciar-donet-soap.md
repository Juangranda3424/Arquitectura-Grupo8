
Correr ese comando en la consola de powershell para iniciar el proyecto en IIS Express. Asegúrate de que la ruta del archivo `applicationhost.config` y los nombres del sitio y del grupo de aplicaciones coincidan con los de tu proyecto.

PS C:\WINDOWS\system32> & "C:\Program Files\IIS Express\iisexpress.exe" `
>>   /config:"C:\Users\JUAN\Desktop\WS_ConversionUnidades_Donet_GR8\.vs\WS_ConversionUnidades_Donet_GR8.slnx\config\applicationhost.config" `
>>   /site:"WS_ConversionUnidades_Donet_GR8" `
>>   /apppool:"Clr4IntegratedAppPool"