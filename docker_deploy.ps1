Write-Host "Building the Angular client Docker image..."
Set-Location -Path "./TheLibraryA"
docker build -t the_library-angular-client:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build the Angular client Docker image"
    exit 1
}
Write-Host "Angular client Docker image built"
Write-Host "Building the Spring server Docker image..."
Set-Location -Path "../TheLibraryj"
docker build -t the_library-spring-server:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build the Spring server Docker image"
    exit 1
}
Write-Host "Spring server Docker image built"
Set-Location -Path ".."

Write-Host "Deploying the Docker stack..."
docker stack deploy --compose-file docker-compose.yml the_library
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to deploy the Docker stack"
    exit 1
}
Write-Host "Docker stack deployed as 'the_library' (without quotes)"