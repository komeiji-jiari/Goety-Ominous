$output = javap -c -p -constants "E:\java\temp_decompile\EntityTypeInit.class" 2>&1 | Out-String
$pattern = "(?s)lambda..static..([0-9]+)\(\).{0,500}?#193.*?areturn"
if ($output -match $pattern) {
    $lambdaNum = $Matches[1]
    Write-Host "lambda$lambdaNum"
    $block = $Matches[0] -split "`n"
    for ($i = 0; $i -lt [Math]::Min(25, $block.Count); $i++) {
        if ($block[$i] -match "m_20699_" -or $block[$i] -match "sized" -or $block[$i] -match "MONSTER|MISC" -or $block[$i] -match "float") {
            Write-Host $block[$i]
        }
    }
} else {
    Write-Host "Not found"
}
