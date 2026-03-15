package co.ryzer.ancla.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.ryzer.ancla.ui.components.AnclaTextField
import co.ryzer.ancla.ui.theme.AnclaBackground
import co.ryzer.ancla.ui.theme.CardGreen
import co.ryzer.ancla.ui.theme.CardLavender
import co.ryzer.ancla.ui.theme.SurfaceWhite
import co.ryzer.ancla.ui.theme.TextPrimary
import co.ryzer.ancla.ui.theme.TextSecondary

@Preview(showBackground = true)
@Composable
fun DecoderScreen() {
    var textToAnalyze by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AnclaBackground)
            .padding(24.dp)
    ) {
        Text(
            text = "Decodificador Social",
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
        Text(
            text = "Analizar Tono",
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Text input area
        AnclaTextField(
            value = textToAnalyze,
            onValueChange = { textToAnalyze = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            placeholder = { Text("Pega el texto aquí...", color = TextSecondary) },
            minLines = 5,
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Lógica de análisis */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = CardGreen),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Analizar", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Resultado:",
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Analysis Results
        AnalysisResultRow("Directo (80%)", 0.8f, CardGreen)
        Spacer(modifier = Modifier.height(12.dp))
        AnalysisResultRow("Broma (20%)", 0.2f, CardLavender)
    }
}

@Composable
fun AnalysisResultRow(label: String, progress: Float, color: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(SurfaceWhite, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Background of the bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight(0.6f)
                    .background(color, RoundedCornerShape(8.dp))
            )
            // Text on top
            Text(
                text = label,
                color = TextPrimary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
    }
}
