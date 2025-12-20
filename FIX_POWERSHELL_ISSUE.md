# Fix PowerShell Execution Policy Issue

## Problem
PowerShell is blocking npm scripts due to execution policy restrictions.

## Solution Options

### Option 1: Bypass for Current Session (Quick Fix - Recommended)

Run this command in PowerShell **as Administrator**:

```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

Then try npm commands again:
```powershell
npm install
npm run dev
```

### Option 2: Bypass for Current Session Only (No Admin Needed)

Run this in your current PowerShell session:

```powershell
Set-ExecutionPolicy -ExecutionPolicy Bypass -Scope Process
```

Then run:
```powershell
npm install
npm run dev
```

**Note**: This only works for the current PowerShell window. You'll need to run it again if you open a new terminal.

### Option 3: Use Command Prompt Instead (Easiest)

1. Close PowerShell
2. Open **Command Prompt** (cmd.exe) instead
3. Navigate to the project:
   ```cmd
   cd "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\ImigishaLink"
   ```
4. Run npm commands:
   ```cmd
   npm install
   npm run dev
   ```

### Option 4: Use PowerShell with Bypass Flag

Run npm with bypass flag:

```powershell
powershell -ExecutionPolicy Bypass -Command "npm install"
powershell -ExecutionPolicy Bypass -Command "npm run dev"
```

### Option 5: Change Execution Policy Permanently (Requires Admin)

1. Open PowerShell **as Administrator** (Right-click → Run as Administrator)
2. Run:
   ```powershell
   Set-ExecutionPolicy RemoteSigned
   ```
3. Type `Y` to confirm
4. Close and reopen PowerShell
5. Try npm commands again

## Recommended Quick Fix

**For immediate use, use Option 3 (Command Prompt)** - it's the fastest and doesn't require admin rights.

## Step-by-Step: Using Command Prompt

1. Press `Win + R`
2. Type `cmd` and press Enter
3. Navigate to project:
   ```cmd
   cd /d "K:\new\AUCA\Semester 7\Web Technology and Internet\Web Tech\ImigishaLink Project\ImigishaLink"
   ```
4. Install dependencies:
   ```cmd
   npm install
   ```
5. Start dev server:
   ```cmd
   npm run dev
   ```

## Verify Fix

After running one of the solutions, you should see:
```
VITE v7.x.x  ready in XXX ms
➜  Local:   http://localhost:3000/
```

Then open your browser to: **http://localhost:3000**

