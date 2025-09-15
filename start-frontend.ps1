# Gurugoppo Frontend Startup Script
# This script will start the React frontend application

Write-Host "🚀 Starting Gurugoppo Frontend..." -ForegroundColor Green

# Check if Node.js is installed
try {
    $nodeVersion = node --version
    Write-Host "✅ Node.js version: $nodeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Node.js is not installed. Please install Node.js 16+ from https://nodejs.org/" -ForegroundColor Red
    exit 1
}

# Check if npm is available
try {
    $npmVersion = npm --version
    Write-Host "✅ npm version: $npmVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ npm is not available. Please install npm." -ForegroundColor Red
    exit 1
}

# Navigate to frontend directory
if (Test-Path "frontend") {
    Set-Location "frontend"
    Write-Host "📁 Changed to frontend directory" -ForegroundColor Blue
} else {
    Write-Host "❌ Frontend directory not found. Please run this script from the project root." -ForegroundColor Red
    exit 1
}

# Check if package.json exists
if (Test-Path "package.json") {
    Write-Host "✅ Found package.json" -ForegroundColor Green
} else {
    Write-Host "❌ package.json not found in frontend directory" -ForegroundColor Red
    exit 1
}

# Install dependencies if node_modules doesn't exist
if (-not (Test-Path "node_modules")) {
    Write-Host "📦 Installing dependencies..." -ForegroundColor Yellow
    npm install
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Dependencies installed successfully" -ForegroundColor Green
    } else {
        Write-Host "❌ Failed to install dependencies" -ForegroundColor Red
        exit 1
    }
} else {
    Write-Host "✅ Dependencies already installed" -ForegroundColor Green
}

# Check if backend is running
Write-Host "🔍 Checking if backend is running..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/posts" -Method GET -TimeoutSec 5 -ErrorAction Stop
    Write-Host "✅ Backend is running on http://localhost:8080" -ForegroundColor Green
} catch {
    Write-Host "⚠️  Backend is not running on http://localhost:8080" -ForegroundColor Yellow
    Write-Host "   Please start the Spring Boot backend first using: mvn spring-boot:run" -ForegroundColor Yellow
    Write-Host "   The frontend will still start but API calls will fail." -ForegroundColor Yellow
}

# Start the React development server
Write-Host "🎯 Starting React development server..." -ForegroundColor Green
Write-Host "   Frontend will be available at: http://localhost:3000" -ForegroundColor Cyan
Write-Host "   Press Ctrl+C to stop the server" -ForegroundColor Cyan
Write-Host ""

npm start
