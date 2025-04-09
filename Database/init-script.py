import datetime
import firebase_admin
from firebase_admin import credentials
from firebase_admin import firestore

def create_firestore_schema_with_mock_data():
    cred = credentials.Certificate('firestore-key.json')

    app = firebase_admin.initialize_app(cred)

    db = firestore.client(database_id="image-db")

    # --------------------- USERS COLLECTION ---------------------
    users_collection = db.collection('users')
    user_docs = [
        {
            "email": "john.doe@example.com",
            "password_hash": "hashed_password_123",
            "name": "John Doe",
            "created_at": datetime.datetime.utcnow(),
            "updated_at": None
        },
        {
            "email": "jane.smith@example.com",
            "password_hash": "hashed_password_456",
            "name": "Jane Smith",
            "created_at": datetime.datetime.utcnow(),
            "updated_at": None
        }
    ]
    user_refs = []
    for user in user_docs:
        doc_ref = users_collection.document()  # auto-generated document ID
        doc_ref.set(user)
        user_refs.append(doc_ref)
    print(f"Created {len(user_refs)} user documents.")

    # --------------------- BACKUPS COLLECTION ---------------------
    backups_collection = db.collection('backups')
    backup_docs = [
        {
            "file_path": "/path/to/backup1.zip",
            "backup_date": datetime.datetime(2025, 4, 5),
        },
        {
            "file_path": "/path/to/backup2.zip",
            "backup_date": datetime.datetime(2025, 4, 10),
        }
    ]
    backup_refs = []
    for backup in backup_docs:
        doc_ref = backups_collection.document()
        doc_ref.set(backup)
        backup_refs.append(doc_ref)
    print(f"Created {len(backup_refs)} backup documents.")

    # --------------------- WATERMARKS COLLECTION ---------------------
    watermarks_collection = db.collection('watermarks')
    watermark_docs = [
        {
            "watermark_image": "/path/to/watermark1.png",
            "created_at": datetime.datetime.utcnow(),
        },
        {
            "watermark_image": "/path/to/watermark2.png",
            "created_at": datetime.datetime.utcnow(),
        }
    ]
    watermark_refs = []
    for watermark in watermark_docs:
        doc_ref = watermarks_collection.document()
        doc_ref.set(watermark)
        watermark_refs.append(doc_ref)
    print(f"Created {len(watermark_refs)} watermark documents.")

    # --------------------- ALBUMS COLLECTION ---------------------
    albums_collection = db.collection('albums')
    album_docs = [
        {
            "name": "Summer Vacation 2025",
            "description": "Photos from my trip to Spain",
            "created_at": datetime.datetime.utcnow(),
        },
        {
            "name": "Family Reunion",
            "description": "Family gathering over the holidays",
            "created_at": datetime.datetime.utcnow(),
        }
    ]
    album_refs = []
    for album in album_docs:
        doc_ref = albums_collection.document()
        doc_ref.set(album)
        album_refs.append(doc_ref)
    print(f"Created {len(album_refs)} album documents.")

    # --------------------- PHOTOS COLLECTION ---------------------
    photos_collection = db.collection('photos')
    photo_docs = [
        {
            "file_path": "photos/spain/beach.jpg",
            "title": "Beach Photo",
            "description": "Beautiful sunny beach in Barcelona",
            "location": "Barcelona, Spain",
            "tags": ["beach", "vacation", "spain"],
            "uploaded_at": datetime.datetime.utcnow(),
        },
        {
            "file_path": "photos/spain/city.jpg",
            "title": "City Landscape",
            "description": "Downtown architecture in Madrid",
            "location": "Madrid, Spain",
            "tags": ["city", "architecture", "spain"],
            "uploaded_at": datetime.datetime.utcnow(),
        },
        {
            "file_path": "photos/family/holiday.jpg",
            "title": "Family Dinner",
            "description": "Group photo at the family reunion dinner",
            "location": "Grandma's House",
            "tags": ["family", "reunion", "holiday"],
            "uploaded_at": datetime.datetime.utcnow(),
        }
    ]
    photo_refs = []
    for photo in photo_docs:
        doc_ref = photos_collection.document()
        doc_ref.set(photo)
        photo_refs.append(doc_ref)
    print(f"Created {len(photo_refs)} photo documents.")

    # --------------------- ALBUM_PHOTOS COLLECTION ---------------------
    # Linking albums and photos by storing document IDs
    album_photos_collection = db.collection('album_photos')
    album_photos_docs = [
        {
            "album_id": album_refs[0].id,
            "photo_id": photo_refs[0].id
        },
        {
            "album_id": album_refs[0].id,
            "photo_id": photo_refs[1].id
        },
        {
            "album_id": album_refs[1].id,
            "photo_id": photo_refs[2].id
        }
    ]
    album_photos_refs = []
    for album_photo in album_photos_docs:
        doc_ref = album_photos_collection.document()
        doc_ref.set(album_photo)
        album_photos_refs.append(doc_ref)
    print(f"Created {len(album_photos_refs)} album_photos documents.")

    # --------------------- PUBLIC GALLERIES COLLECTION ---------------------
    public_galleries_collection = db.collection('public_galleries')
    public_galleries_docs = [
        {
            "public_link": "https://myapp.com/gallery/abc123",
            "created_at": datetime.datetime.utcnow(),
        },
        {
            "public_link": "https://myapp.com/gallery/def456",
            "created_at": datetime.datetime.utcnow(),
        }
    ]
    public_galleries_refs = []
    for gallery in public_galleries_docs:
        doc_ref = public_galleries_collection.document()
        doc_ref.set(gallery)
        public_galleries_refs.append(doc_ref)
    print(f"Created {len(public_galleries_refs)} public gallery documents.")

    # --------------------- CLOUD INTEGRATIONS COLLECTION ---------------------
    cloud_integrations_collection = db.collection('cloud_integrations')
    cloud_integrations_docs = [
        {
            "service_name": "Dropbox",
            "access_token": "dropbox_test_token_123",
            "created_at": datetime.datetime.utcnow(),
        },
        {
            "service_name": "Google Drive",
            "access_token": "google_drive_test_token_456",
            "created_at": datetime.datetime.utcnow(),
        }
    ]
    cloud_integration_refs = []
    for integration in cloud_integrations_docs:
        doc_ref = cloud_integrations_collection.document()
        doc_ref.set(integration)
        cloud_integration_refs.append(doc_ref)
    print(f"Created {len(cloud_integration_refs)} cloud_integration documents.")

if __name__ == "__main__":
    create_firestore_schema_with_mock_data()
    print("Firestore mock schema and data creation complete!")
