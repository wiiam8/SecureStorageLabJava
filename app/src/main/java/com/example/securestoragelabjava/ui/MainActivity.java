package com.example.securestoragelabjava.ui;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.SwitchCompat;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.securestoragelabjava.R;
import com.example.securestoragelabjava.cache.CacheStore;
import com.example.securestoragelabjava.external.ExternalAppFilesStore;
import com.example.securestoragelabjava.files.InternalTextStore;
import com.example.securestoragelabjava.files.StudentsJsonStore;
import com.example.securestoragelabjava.model.Student;
import com.example.securestoragelabjava.prefs.AppPrefs;
import com.example.securestoragelabjava.prefs.SecurePrefs;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "SecureStorageJava";

    private static final String NOTE_FILE = "note.txt";
    private static final String CACHE_FILE = "last_ui.txt";
    private static final String EXTERNAL_FILE = "export.txt";

    private final List<String> langs = Arrays.asList("fr", "en", "ar");

    private EditText etName;
    private EditText etToken;
    private Spinner spLang;
    private SwitchCompat swDark;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = findViewById(R.id.etName);
        etToken = findViewById(R.id.etToken);
        spLang = findViewById(R.id.spLang);
        swDark = findViewById(R.id.swDark);
        tvResult = findViewById(R.id.tvResult);

        setupLangSpinner();

        Button btnSavePrefs = findViewById(R.id.btnSavePrefs);
        Button btnLoadPrefs = findViewById(R.id.btnLoadPrefs);
        Button btnSaveJson = findViewById(R.id.btnSaveJson);
        Button btnLoadJson = findViewById(R.id.btnLoadJson);
        Button btnWriteCache = findViewById(R.id.btnWriteCache);
        Button btnReadCache = findViewById(R.id.btnReadCache);
        Button btnExportExternal = findViewById(R.id.btnExportExternal);
        Button btnReadExternal = findViewById(R.id.btnReadExternal);
        Button btnClear = findViewById(R.id.btnClear);

        btnSavePrefs.setOnClickListener(v -> savePrefs());
        btnLoadPrefs.setOnClickListener(v -> loadPrefsToUi());
        btnSaveJson.setOnClickListener(v -> saveJsonFile());
        btnLoadJson.setOnClickListener(v -> loadJsonFile());
        btnWriteCache.setOnClickListener(v -> writeCacheFile());
        btnReadCache.setOnClickListener(v -> readCacheFile());
        btnExportExternal.setOnClickListener(v -> exportExternalFile());
        btnReadExternal.setOnClickListener(v -> readExternalFile());
        btnClear.setOnClickListener(v -> clearAll());

        loadPrefsToUi();
    }

    private void setupLangSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                langs
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLang.setAdapter(adapter);
    }

    private void savePrefs() {
        String name = etName.getText().toString().trim();
        String lang = langs.get(Math.max(0, spLang.getSelectedItemPosition()));
        String theme = swDark.isChecked() ? "dark" : "light";

        boolean ok = AppPrefs.save(this, name, lang, theme, false);

        String token = etToken.getText().toString();

        if (!TextUtils.isEmpty(token)) {
            try {
                SecurePrefs.saveToken(this, token);
            } catch (Exception e) {
                tvResult.setText("Erreur chiffrement token. Vérifier Security Crypto.");
                Log.e(TAG, "Erreur SecurePrefs saveToken: " + e.getClass().getSimpleName());
                return;
            }
        }

        try {
            CacheStore.write(this, CACHE_FILE, "name=" + name + ", lang=" + lang + ", theme=" + theme);
        } catch (Exception ignored) {
        }

        Log.d(TAG, "Prefs sauvegardées ok=" + ok + ", name=" + name + ", lang=" + lang + ", theme=" + theme + ", tokenLength=" + token.length());

        tvResult.setText(
                "Sauvegarde prefs terminée.\n" +
                        "name=" + name + "\n" +
                        "lang=" + lang + "\n" +
                        "theme=" + theme + "\n" +
                        "token: stocké chiffré si non vide.\n" +
                        "tokenLength=" + token.length() + "\n\n" +
                        "Important: le token n'est jamais affiché en clair."
        );
    }

    private void loadPrefsToUi() {
        AppPrefs.Triple triple = AppPrefs.load(this);

        etName.setText(triple.name);
        swDark.setChecked("dark".equals(triple.theme));

        int index = langs.indexOf(triple.lang);
        spLang.setSelection(index >= 0 ? index : 0);

        int tokenLength = 0;

        try {
            String token = SecurePrefs.loadToken(this);
            tokenLength = token == null ? 0 : token.length();
        } catch (Exception ignored) {
        }

        tvResult.setText(
                "Chargement prefs terminé.\n" +
                        "name=" + triple.name + "\n" +
                        "lang=" + triple.lang + "\n" +
                        "theme=" + triple.theme + "\n" +
                        "tokenLength=" + tokenLength
        );

        Log.d(TAG, "Prefs chargées name=" + triple.name + ", lang=" + triple.lang + ", theme=" + triple.theme + ", tokenLength=" + tokenLength);
    }

    private void saveJsonFile() {
        List<Student> students = Arrays.asList(
                new Student(1, "Amina", 20),
                new Student(2, "Omar", 21),
                new Student(3, "Sara", 19)
        );

        try {
            StudentsJsonStore.save(this, students);
            InternalTextStore.writeUtf8(this, NOTE_FILE, "Sauvegarde JSON effectuée en UTF-8.");
        } catch (Exception e) {
            tvResult.setText("Erreur sauvegarde JSON.");
            Log.e(TAG, "Erreur saveJsonFile: " + e.getClass().getSimpleName());
            return;
        }

        Log.d(TAG, "Fichiers internes écrits: students.json, note.txt");

        tvResult.setText(
                "Sauvegarde fichiers internes terminée.\n" +
                        "students.json créé.\n" +
                        "note.txt créé.\n" +
                        "students=" + students.size()
        );
    }

    private void loadJsonFile() {
        List<Student> students = StudentsJsonStore.load(this);

        String note;

        try {
            note = InternalTextStore.readUtf8(this, NOTE_FILE);
        } catch (Exception e) {
            note = "(note.txt absent)";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Chargement fichiers internes terminé.\n");
        sb.append("note=").append(note).append("\n");
        sb.append("students=").append(students.size()).append("\n");

        for (Student student : students) {
            sb.append(" - id=").append(student.id)
                    .append(", name=").append(student.name)
                    .append(", age=").append(student.age)
                    .append("\n");
        }

        tvResult.setText(sb.toString());
        Log.d(TAG, "Fichier JSON chargé: students=" + students.size());
    }

    private void writeCacheFile() {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        String content = "Dernier état UI temporaire généré le " + now;

        try {
            CacheStore.write(this, CACHE_FILE, content);
            tvResult.setText(
                    "Cache écrit avec succès.\n" +
                            "Fichier: " + CACHE_FILE + "\n" +
                            "Contenu: " + content
            );
            Log.d(TAG, "Cache écrit: " + CACHE_FILE);
        } catch (Exception e) {
            tvResult.setText("Erreur écriture cache.");
            Log.e(TAG, "Erreur writeCacheFile: " + e.getClass().getSimpleName());
        }
    }

    private void readCacheFile() {
        try {
            String content = CacheStore.read(this, CACHE_FILE);

            if (content == null) {
                tvResult.setText("Cache absent.");
            } else {
                tvResult.setText(
                        "Lecture cache terminée.\n" +
                                "Fichier: " + CACHE_FILE + "\n" +
                                "Contenu: " + content
                );
            }
        } catch (Exception e) {
            tvResult.setText("Erreur lecture cache.");
            Log.e(TAG, "Erreur readCacheFile: " + e.getClass().getSimpleName());
        }
    }

    private void exportExternalFile() {
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        String content =
                "Export app-specific externe\n" +
                        "Date=" + now + "\n" +
                        "Aucune donnée sensible exportée.";

        try {
            String path = ExternalAppFilesStore.write(this, EXTERNAL_FILE, content);

            tvResult.setText(
                    "Export externe terminé.\n" +
                            "Fichier: " + EXTERNAL_FILE + "\n" +
                            "Chemin:\n" + path
            );

            Log.d(TAG, "Export externe app-specific écrit: " + path);
        } catch (Exception e) {
            tvResult.setText("Erreur export externe.");
            Log.e(TAG, "Erreur exportExternalFile: " + e.getClass().getSimpleName());
        }
    }

    private void readExternalFile() {
        try {
            String content = ExternalAppFilesStore.read(this, EXTERNAL_FILE);

            if (content == null) {
                tvResult.setText("Fichier externe absent.");
            } else {
                tvResult.setText(
                        "Lecture externe terminée.\n" +
                                "Fichier: " + EXTERNAL_FILE + "\n" +
                                "Contenu:\n" + content
                );
            }
        } catch (Exception e) {
            tvResult.setText("Erreur lecture externe.");
            Log.e(TAG, "Erreur readExternalFile: " + e.getClass().getSimpleName());
        }
    }

    private void clearAll() {
        AppPrefs.clear(this);

        try {
            SecurePrefs.clear(this);
        } catch (Exception ignored) {
        }

        StudentsJsonStore.delete(this);
        InternalTextStore.delete(this, NOTE_FILE);

        int purged = CacheStore.purge(this);

        ExternalAppFilesStore.delete(this, EXTERNAL_FILE);

        etName.setText("");
        etToken.setText("");
        swDark.setChecked(false);
        spLang.setSelection(0);

        tvResult.setText(
                "Nettoyage terminé.\n" +
                        "prefs: clear()\n" +
                        "secure_prefs: clear()\n" +
                        "students.json: delete\n" +
                        "note.txt: delete\n" +
                        "cache purgé: " + purged + " fichier(s)\n" +
                        "export externe: delete\n\n" +
                        "Aucune donnée sensible loggée."
        );

        Log.d(TAG, "Nettoyage terminé. Aucun secret loggé.");
    }
}