package aplicacionesmoviles.avanzado.todosalau.wifiexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Button btnGetLocation;
    private TextView tvLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean isGettingLocation = false; // Control de estado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetLocation = findViewById(R.id.btnGetLocation);
        tvLocation = findViewById(R.id.tvLocation);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isGettingLocation) {
                    // Si no se está obteniendo la ubicación, comenzar a obtenerla
                    if (checkPermission()) {
                        startGettingLocation();
                    } else {
                        requestPermission();
                    }
                } else {
                    // Si se está obteniendo la ubicación, detenerla
                    stopGettingLocation();
                }
            }
        });
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    private void startGettingLocation() {
        isGettingLocation = true; // Establecer el estado como obteniendo ubicación
        btnGetLocation.setText("Detener Obtención"); // Cambiar el texto del botón
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude(); // Obtener altitud
                float speed = location.getSpeed(); // Obtener velocidad en metros/segundo

                // Convertir la velocidad a km/h (1 m/s = 3.6 km/h)
                float speedKmh = speed * 3.6f;

                // Mostrar la ubicación, altitud y velocidad
                tvLocation.setText("Ubicación: \n Latitud " + latitude + ", \n Longitud " + longitude +
                        ", \n Altitud: " + altitude + " metros, " +
                        "\n Velocidad: " + speedKmh + " km/h, "+ speed + " m/s");
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {}

            @Override
            public void onProviderDisabled(@NonNull String provider) {}
        };

        // Solicitar actualizaciones de ubicación a través del GPS
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
            Toast.makeText(this, "No se puede acceder a la ubicación.", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopGettingLocation() {
        isGettingLocation = false ; // Establecer el estado como no obteniendo ubicación
        btnGetLocation.setText("Obtener Ubicación"); // Cambiar el texto del botón
        if (locationManager != null && locationListener != null) {
            locationManager.removeUpdates(locationListener); // Detener las actualizaciones de ubicación
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Asegurarse de detener la obtención de ubicación al destruir la actividad
        stopGettingLocation();
    }
}