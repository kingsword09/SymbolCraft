package io.github.kingsword09.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

import io.github.kingsword09.example.icons.materialsymbols.Icons as MaterialSymbols
import io.github.kingsword09.example.icons.materialsymbols.icons.HomeW400Rounded
import io.github.kingsword09.example.icons.materialsymbols.icons.HomeW400Outlined
import io.github.kingsword09.example.icons.materialsymbols.icons.HomeW500Rounded
import io.github.kingsword09.example.icons.materialsymbols.icons.HomeW400OutlinedFill
import io.github.kingsword09.example.icons.materialsymbols.icons.SearchW400Outlined
import io.github.kingsword09.example.icons.materialsymbols.icons.SearchW500Outlined
import io.github.kingsword09.example.icons.materialsymbols.icons.SearchW700Outlined
import io.github.kingsword09.example.icons.materialsymbols.icons.SettingsW400Outlined
import io.github.kingsword09.example.icons.materialsymbols.icons.SettingsW500RoundedFill
import io.github.kingsword09.example.icons.materialsymbols.icons.PersonW500Outlined
import io.github.kingsword09.example.icons.materialsymbols.icons.PersonW500Sharp
import io.github.kingsword09.example.icons.materialsymbols.icons.PersonW500Rounded
import io.github.kingsword09.example.icons.mdi.Icons as MdiIcons
import io.github.kingsword09.example.icons.mdi.icons.AbTestingMdi
import io.github.kingsword09.example.icons.mdi.icons.AbacusMdi

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.HomeW400Rounded,
                    contentDescription = "Home",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Home (Weight 400, Rounded)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.HomeW400Outlined,
                    contentDescription = "Home",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Home (Weight 400, Outlined)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.HomeW400OutlinedFill,
                    contentDescription = "Home",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Home (Weight 400, Outlined, Fill)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.HomeW400Rounded,
                    contentDescription = "Home",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Home (Weight 400, Rounded)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.HomeW500Rounded,
                    contentDescription = "Home",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Home (Weight 500, Rounded)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.PersonW500Sharp,
                    contentDescription = "Person",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Person (Weight 500, Sharp)")
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.PersonW500Rounded,
                    contentDescription = "Person",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Person (Weight 500, Rounded)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.PersonW500Outlined,
                    contentDescription = "Person",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Person (Weight 500, Outlined)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.SearchW400Outlined,
                    contentDescription = "Search",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Search (Weight 400, Outlined)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.SearchW500Outlined,
                    contentDescription = "Search",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Search (Weight 500, Outlined)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.SearchW700Outlined,
                    contentDescription = "Search",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Search (Weight 700, Outlined)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.SettingsW400Outlined,
                    contentDescription = "Settings",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Settings (Weight 400, Outlined)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MaterialSymbols.SettingsW500RoundedFill,
                    contentDescription = "Settings",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Settings (Weight 500, Rounded, Fill)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MdiIcons.AbTestingMdi,
                    contentDescription = "AbTest",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("AB Testing Icon (MDI External Library)")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MdiIcons.AbacusMdi,
                    contentDescription = "Abacus",
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Text("Abacus Icon (MDI External Library)")
            }
        }
    }
}