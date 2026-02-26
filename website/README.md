# Bank Management System - Landing Page

This is the landing page for the Bank Management System Java application.

## Deploy to Netlify

### Method 1: Drag & Drop (Easiest)
1. Go to [Netlify Drop](https://app.netlify.com/drop)
2. Drag and drop the entire `website` folder
3. Done! Your site will be live instantly

### Method 2: GitHub Integration
1. Go to [Netlify](https://app.netlify.com)
2. Click "Add new site" → "Import an existing project"
3. Connect to GitHub
4. Select: `Rushi22007/Bank-Management-System-Java`
5. Set build settings:
   - Base directory: `website`
   - Publish directory: `website`
6. Click "Deploy site"

### Method 3: Netlify CLI
```bash
# Install Netlify CLI
npm install -g netlify-cli

# Navigate to website folder
cd website

# Login to Netlify
netlify login

# Deploy
netlify deploy --prod
```

## Files Included
- `index.html` - Main landing page
- `style.css` - Styling
- `script.js` - Interactive features
- `netlify.toml` - Netlify configuration

## Features
✅ Modern, responsive design
✅ Download buttons for JAR file
✅ Links to GitHub repository
✅ Features showcase
✅ Installation instructions
✅ System requirements
✅ Demo credentials display

## Customization
Edit `index.html` to:
- Update GitHub links
- Modify content
- Add screenshots
- Change colors in `style.css`

## After Deployment
Your landing page will be available at: `https://your-site-name.netlify.app`

You can then:
- Set up a custom domain
- Configure HTTPS (automatic)
- Set up continuous deployment
