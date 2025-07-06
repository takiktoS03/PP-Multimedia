using Google.Cloud.Firestore;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Aplikacja_desktopowa.Service
{
    /// <summary>
    /// Klasa odpowiedzialna za kopiowanie danych między dwoma bazami Firestore.
    /// </summary>
    class FirestoreCopier
    {
        /// <summary>
        /// Obiekt reprezentujący źródłową bazę Firestore.
        /// </summary>
        private FirestoreDb sourceDb;

        /// <summary>
        /// Obiekt reprezentujący docelową bazę Firestore.
        /// </summary>
        private FirestoreDb targetDb;

        /// <summary>
        /// Metoda uruchamiająca kopiowanie danych z wybranych kolekcji
        /// ze źródłowej bazy do docelowej.
        /// </summary>
        public async Task CopyAsync()
        {
            // Ustawienie ścieżki do pliku z poświadczeniami Firebase
            string credentialsPath = Path.Combine(AppDomain.CurrentDomain.BaseDirectory, "image-management-cbaee-firebase-adminsdk-fbsvc-534514b3a5.json");
            Environment.SetEnvironmentVariable("GOOGLE_APPLICATION_CREDENTIALS", credentialsPath);

            // Inicjalizacja połączenia do źródłowej bazy Firestore o nazwie "image-db"
            sourceDb = new FirestoreDbBuilder
            {
                ProjectId = "image-management-cbaee",
                DatabaseId = "image-db"
            }.Build();

            // Inicjalizacja połączenia do docelowej bazy Firestore (domyślna baza)
            targetDb = new FirestoreDbBuilder
            {
                ProjectId = "image-management-cbaee",
                DatabaseId = "(default)"
            }.Build();

            // Tablica nazw kolekcji, które mają zostać skopiowane
            string[] collections = new string[] { "users" };

            // Iteracja po kolekcjach i kopiowanie danych
            foreach (string collection in collections)
            {
                await CopyCollectionAsync(collection);
            }

            //Console.WriteLine("Kopiowanie zakończone!");
        }

        /// <summary>
        /// Metoda kopiująca wszystkie dokumenty z jednej kolekcji źródłowej
        /// do odpowiadającej kolekcji w bazie docelowej.
        /// </summary>
        /// <param name="collectionName">Nazwa kolekcji do skopiowania.</param>
        private async Task CopyCollectionAsync(string collectionName)
        {
            //Console.WriteLine($"Kopiowanie kolekcji: {collectionName}");

            CollectionReference sourceCollection = sourceDb.Collection(collectionName);
            CollectionReference targetCollection = targetDb.Collection(collectionName);

            // Pobranie wszystkich dokumentów z kolekcji źródłowej
            QuerySnapshot snapshot = await sourceCollection.GetSnapshotAsync();

            // Kopiowanie każdego dokumentu do kolekcji docelowej z zachowaniem identyfikatora
            foreach (DocumentSnapshot doc in snapshot.Documents)
            {
                Dictionary<string, object> data = doc.ToDictionary();
                await targetCollection.Document(doc.Id).SetAsync(data);
            }
        }
    }
}
