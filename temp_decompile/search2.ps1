$output = javap -c -p -constants "E:\java\temp_decompile\EntityTypeInit.class" 2>&1 | Out-String
$lines = $output -split "`r`n"
for ($i = 0; $i -lt $lines.Count; $i++) {
    if ($lines[$i] -match "private static.*lambda") {
        $name = ""
        $sizedW = $sizedH = ""
        $cat = ""
        for ($j = $i; $j -lt [Math]::Min($i+25, $lines.Count); $j++) {
            if ($lines[$j] -match "m_20699_.*?([0-9.]+)f.*?([0-9.]+)f") { $sizedW = $Matches[1]; $sizedH = $Matches[2] }
            if ($lines[$j] -match "MONSTER") { $cat = "MONSTER" }
            elseif ($lines[$j] -match "MISC") { $cat = "MISC" }
            if ($lines[$j] -match "// String (\w+)") { $name = $Matches[1] }
            if ($lines[$j] -match "String (\w+)" -and $name -eq "") { if ($Matches[1] -ne "mutantmore") { $name = $Matches[1] } }
        }
        Write-Host "$($lines[$i].Trim()) -> $name`tsized($sizedW, $sizedH) $cat"
    }
}
