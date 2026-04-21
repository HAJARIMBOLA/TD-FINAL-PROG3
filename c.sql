-- =====================================================================
-- FÉDÉRATION AGRICOLE MADAGASCAR - Schéma base de données
-- V1 - Migration initiale
-- =====================================================================

-- -------------------------------------------------------
-- TYPES ENUM PostgreSQL
-- -------------------------------------------------------
CREATE TYPE genre_enum AS ENUM ('MASCULIN', 'FEMININ');

CREATE TYPE secteur_agricole_enum AS ENUM (
    'AGRICULTURE_VIVRIERE', 'AGRICULTURE_COMMERCIALE',
    'ELEVAGE', 'PECHE', 'SYLVICULTURE',
    'AGRO_INDUSTRIE', 'HORTICULTURE', 'MIXTE'
);

CREATE TYPE metier_enum AS ENUM (
    'PRODUCTEUR',       -- cultive directement (riziculteur, maraîcher...)
    'COLLECTEUR',       -- achète sur pied et revend avec marge
    'TRANSFORMATEUR',
    'EXPORTATEUR',
    'DISTRIBUTEUR',
    'TECHNICIEN_AGRICOLE',
    'AUTRE'
);

CREATE TYPE poste_collectivite_enum AS ENUM (
    'PRESIDENT', 'PRESIDENT_ADJOINT', 'TRESORIER',
    'SECRETAIRE', 'MEMBRE_CONFIRME', 'MEMBRE_JUNIOR'
);

CREATE TYPE poste_federation_enum AS ENUM (
    'PRESIDENT', 'PRESIDENT_ADJOINT', 'TRESORIER', 'SECRETAIRE'
);

CREATE TYPE statut_membre_enum AS ENUM ('ACTIF', 'DEMISSIONNE', 'SUSPENDU');

CREATE TYPE type_compte_enum AS ENUM ('CAISSE', 'BANCAIRE', 'MOBILE_MONEY');

CREATE TYPE mode_paiement_enum AS ENUM ('ESPECE', 'VIREMENT_BANCAIRE', 'MOBILE_MONEY');

CREATE TYPE type_cotisation_enum AS ENUM ('MENSUELLE', 'ANNUELLE', 'PONCTUELLE');

CREATE TYPE nom_banque_enum AS ENUM (
    'BRED', 'MCB', 'BMOI', 'BOA', 'BGFI',
    'AFG', 'ACCES_BANQUE', 'BAOBAB', 'SIPEM'
);

CREATE TYPE service_mobile_money_enum AS ENUM ('ORANGE_MONEY', 'MVOLA', 'AIRTEL_MONEY');

CREATE TYPE statut_activite_enum AS ENUM ('PLANIFIEE', 'EN_COURS', 'TERMINEE', 'ANNULEE');

CREATE TYPE type_activite_enum AS ENUM (
    'ASSEMBLEE_GENERALE_MENSUELLE',
    'FORMATION_OBLIGATOIRE_JUNIORS',
    'EXCEPTIONNELLE',
    'FEDERATION'
);

CREATE TYPE statut_presence_enum AS ENUM ('PRESENT', 'ABSENT', 'EXCUSE');

CREATE TYPE statut_demande_enum AS ENUM ('EN_ATTENTE', 'APPROUVEE', 'REJETEE');

-- -------------------------------------------------------
-- VILLE
-- -------------------------------------------------------
CREATE TABLE ville (
    id      BIGSERIAL PRIMARY KEY,
    nom     VARCHAR(100) NOT NULL UNIQUE,
    region  VARCHAR(100),
    province VARCHAR(100)
);

-- -------------------------------------------------------
-- DOMAINE DE SPÉCIALISATION
-- Distingue secteur > domaine > métier
-- Ex: secteur=AGRICULTURE_VIVRIERE, domaine=Riziculture
-- -------------------------------------------------------
CREATE TABLE domaine_specialisation (
    id          BIGSERIAL PRIMARY KEY,
    nom         VARCHAR(150) NOT NULL,
    description TEXT,
    secteur     secteur_agricole_enum NOT NULL
);

-- -------------------------------------------------------
-- COLLECTIVITÉ AGRICOLE
-- -------------------------------------------------------
CREATE TABLE collectivite (
    id                        BIGSERIAL PRIMARY KEY,
    numero                    VARCHAR(50)  NOT NULL UNIQUE,
    nom                       VARCHAR(200) NOT NULL UNIQUE,
    ville_id                  BIGINT       NOT NULL REFERENCES ville(id),
    domaine_specialisation_id BIGINT       NOT NULL REFERENCES domaine_specialisation(id),
    date_creation             DATE         NOT NULL,
    active                    BOOLEAN      NOT NULL DEFAULT TRUE
);

-- -------------------------------------------------------
-- MEMBRE
-- -------------------------------------------------------
CREATE TABLE membre (
    id                         BIGSERIAL PRIMARY KEY,
    nom                        VARCHAR(100) NOT NULL,
    prenom                     VARCHAR(100) NOT NULL,
    date_naissance             DATE         NOT NULL,
    genre                      genre_enum   NOT NULL,
    adresse                    TEXT         NOT NULL,
    metier_exerce              metier_enum  NOT NULL,
    telephone                  VARCHAR(20)  NOT NULL,
    email                      VARCHAR(150) NOT NULL UNIQUE,
    date_adhesion_federation   DATE         NOT NULL,
    statut                     statut_membre_enum NOT NULL DEFAULT 'ACTIF',
    date_demission             DATE,
    collectivite_id            BIGINT       REFERENCES collectivite(id),
    poste_actuel               poste_collectivite_enum,
    parrain_id                 BIGINT       REFERENCES membre(id),
    mot_de_passe_hash          VARCHAR(255),
    compte_actif               BOOLEAN      NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_demission CHECK (
        statut != 'DEMISSIONNE' OR date_demission IS NOT NULL
    )
);

CREATE INDEX idx_membre_collectivite ON membre(collectivite_id);
CREATE INDEX idx_membre_email ON membre(email);
CREATE INDEX idx_membre_statut ON membre(statut);

-- -------------------------------------------------------
-- MANDAT COLLECTIVITÉ (1 an)
-- Un membre ne peut occuper un poste spécifique plus de 2 mandats
-- -------------------------------------------------------
CREATE TABLE mandat_collectivite (
    id              BIGSERIAL PRIMARY KEY,
    membre_id       BIGINT NOT NULL REFERENCES membre(id),
    collectivite_id BIGINT NOT NULL REFERENCES collectivite(id),
    poste           poste_collectivite_enum NOT NULL,
    annee_mandat    INT    NOT NULL,
    date_debut      DATE   NOT NULL,
    date_fin        DATE,
    actif           BOOLEAN NOT NULL DEFAULT TRUE,

    -- Un seul actif par poste spécifique par collectivité par an
    CONSTRAINT uq_poste_specifique_actif
        UNIQUE NULLS NOT DISTINCT (collectivite_id, poste, annee_mandat)
        DEFERRABLE INITIALLY DEFERRED
);

-- Vérification métier: max 2 mandats par poste spécifique par membre
-- Implémentée au niveau applicatif (service)

CREATE INDEX idx_mandat_collectivite ON mandat_collectivite(collectivite_id, annee_mandat);
CREATE INDEX idx_mandat_membre ON mandat_collectivite(membre_id);

-- -------------------------------------------------------
-- MANDAT FÉDÉRATION (2 ans)
-- -------------------------------------------------------
CREATE TABLE mandat_federation (
    id          BIGSERIAL PRIMARY KEY,
    membre_id   BIGINT NOT NULL REFERENCES membre(id),
    poste       poste_federation_enum NOT NULL,
    annee_debut INT    NOT NULL,
    date_debut  DATE   NOT NULL,
    date_fin    DATE,
    actif       BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT uq_poste_federation_actif
        UNIQUE NULLS NOT DISTINCT (poste, annee_debut)
);

CREATE INDEX idx_mandat_federation_membre ON mandat_federation(membre_id);

-- -------------------------------------------------------
-- COMPTES (héritage single-table)
-- -------------------------------------------------------
CREATE TABLE compte (
    id                      BIGSERIAL PRIMARY KEY,
    dtype                   type_compte_enum NOT NULL,
    solde                   NUMERIC(18,2) NOT NULL DEFAULT 0,
    devise                  VARCHAR(10) NOT NULL DEFAULT 'MGA',
    date_mise_a_jour_solde  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    collectivite_id         BIGINT REFERENCES collectivite(id),
    appartient_federation   BOOLEAN NOT NULL DEFAULT FALSE,
    actif                   BOOLEAN NOT NULL DEFAULT TRUE,

    -- Champs BANCAIRE
    nom_titulaire           VARCHAR(200),
    banque                  nom_banque_enum,
    numero_rib              CHAR(23) UNIQUE,

    -- Champs MOBILE MONEY
    service_mobile_money    service_mobile_money_enum,
    numero_de_telephone     VARCHAR(20) UNIQUE,

    -- Une seule caisse par entité
    CONSTRAINT uq_caisse_collectivite
        EXCLUDE (collectivite_id WITH =)
        WHERE (dtype = 'CAISSE' AND collectivite_id IS NOT NULL),

    CONSTRAINT uq_caisse_federation
        EXCLUDE (appartient_federation WITH =)
        WHERE (dtype = 'CAISSE' AND appartient_federation = TRUE),

    CONSTRAINT chk_bancaire CHECK (
        dtype != 'BANCAIRE' OR (nom_titulaire IS NOT NULL AND banque IS NOT NULL AND numero_rib IS NOT NULL)
    ),
    CONSTRAINT chk_mobile_money CHECK (
        dtype != 'MOBILE_MONEY' OR (service_mobile_money IS NOT NULL AND numero_de_telephone IS NOT NULL)
    ),
    CONSTRAINT chk_proprietaire CHECK (
        (collectivite_id IS NOT NULL AND NOT appartient_federation)
        OR (collectivite_id IS NULL AND appartient_federation)
    )
);

CREATE INDEX idx_compte_collectivite ON compte(collectivite_id);

-- -------------------------------------------------------
-- COTISATION (Section C)
-- -------------------------------------------------------
CREATE TABLE cotisation (
    id                         BIGSERIAL PRIMARY KEY,
    membre_id                  BIGINT NOT NULL REFERENCES membre(id),
    collectivite_id            BIGINT NOT NULL REFERENCES collectivite(id),
    type_cotisation            type_cotisation_enum NOT NULL,
    montant                    NUMERIC(18,2) NOT NULL CHECK (montant > 0),
    date_encaissement          DATE NOT NULL,
    mode_paiement              mode_paiement_enum NOT NULL,
    reference_transaction      VARCHAR(100),
    motif                      TEXT,
    montant_reversee_federation NUMERIC(18,2),
    compte_id                  BIGINT REFERENCES compte(id)
);

CREATE INDEX idx_cotisation_membre ON cotisation(membre_id);
CREATE INDEX idx_cotisation_collectivite ON cotisation(collectivite_id);
CREATE INDEX idx_cotisation_date ON cotisation(date_encaissement);

-- -------------------------------------------------------
-- ACTIVITÉ (Section E)
-- -------------------------------------------------------
CREATE TABLE activite (
    id                      BIGSERIAL PRIMARY KEY,
    titre                   VARCHAR(200) NOT NULL,
    description             TEXT,
    type_activite           type_activite_enum NOT NULL,
    statut                  statut_activite_enum NOT NULL DEFAULT 'PLANIFIEE',
    date_debut              TIMESTAMP NOT NULL,
    date_fin                TIMESTAMP,
    lieu                    VARCHAR(200),
    obligatoire_pour_tous   BOOLEAN NOT NULL DEFAULT FALSE,
    obligatoire_pour_juniors BOOLEAN NOT NULL DEFAULT FALSE,
    collectivite_id         BIGINT REFERENCES collectivite(id),
    activite_federation     BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_organisateur CHECK (
        (collectivite_id IS NOT NULL AND NOT activite_federation)
        OR (collectivite_id IS NULL AND activite_federation)
    )
);

CREATE INDEX idx_activite_collectivite ON activite(collectivite_id);
CREATE INDEX idx_activite_date ON activite(date_debut);

-- -------------------------------------------------------
-- CALENDRIER ACTIVITÉS RÉCURRENTES (Section E)
-- -------------------------------------------------------
CREATE TABLE calendrier_activite (
    id                BIGSERIAL PRIMARY KEY,
    collectivite_id   BIGINT REFERENCES collectivite(id),
    type_activite     type_activite_enum NOT NULL,
    annee             INT NOT NULL,
    jour_semaine      VARCHAR(20) NOT NULL,   -- ex: SUNDAY, SATURDAY
    occurrence_du_mois INT NOT NULL,          -- ex: 2 pour 2ème dimanche
    description       TEXT,

    CONSTRAINT uq_calendrier UNIQUE (collectivite_id, type_activite, annee)
);

-- -------------------------------------------------------
-- PRÉSENCE (Section F)
-- -------------------------------------------------------
CREATE TABLE presence (
    id              BIGSERIAL PRIMARY KEY,
    activite_id     BIGINT NOT NULL REFERENCES activite(id),
    membre_id       BIGINT NOT NULL REFERENCES membre(id),
    statut          statut_presence_enum NOT NULL DEFAULT 'ABSENT',
    motif_absence   TEXT,
    membre_externe  BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT uq_presence UNIQUE (activite_id, membre_id)
);

CREATE INDEX idx_presence_activite ON presence(activite_id);
CREATE INDEX idx_presence_membre ON presence(membre_id);

-- -------------------------------------------------------
-- DEMANDE CRÉATION COLLECTIVITÉ (Section A)
-- -------------------------------------------------------
CREATE TABLE demande_creation_collectivite (
    id                         BIGSERIAL PRIMARY KEY,
    nom_collectivite_proposee  VARCHAR(200) NOT NULL,
    ville_id                   BIGINT NOT NULL REFERENCES ville(id),
    domaine_specialisation_id  BIGINT NOT NULL REFERENCES domaine_specialisation(id),
    statut                     statut_demande_enum NOT NULL DEFAULT 'EN_ATTENTE',
    date_demande               DATE NOT NULL DEFAULT CURRENT_DATE,
    date_decision              DATE,
    commentaire_federation     TEXT,
    collectivite_creee_id      BIGINT REFERENCES collectivite(id)
);

CREATE TABLE demande_membres_fondateurs (
    demande_id BIGINT NOT NULL REFERENCES demande_creation_collectivite(id),
    membre_id  BIGINT NOT NULL REFERENCES membre(id),
    PRIMARY KEY (demande_id, membre_id)
);

-- =====================================================================
-- DONNÉES DE RÉFÉRENCE INITIALES
-- =====================================================================

-- Villes principales de Madagascar
INSERT INTO ville (nom, region, province) VALUES
    ('Antananarivo', 'Analamanga', 'Antananarivo'),
    ('Toamasina', 'Atsinanana', 'Toamasina'),
    ('Antsirabe', 'Vakinankaratra', 'Antananarivo'),
    ('Fianarantsoa', 'Haute Matsiatra', 'Fianarantsoa'),
    ('Mahajanga', 'Boeny', 'Mahajanga'),
    ('Toliara', 'Atsimo-Andrefana', 'Toliara'),
    ('Antsiranana', 'Diana', 'Antsiranana'),
    ('Ambositra', 'Amoron''i Mania', 'Fianarantsoa'),
    ('Morondava', 'Menabe', 'Toliara'),
    ('Sambava', 'SAVA', 'Antsiranana');

-- Domaines de spécialisation agricole (secteur > domaine)
INSERT INTO domaine_specialisation (nom, description, secteur) VALUES
    ('Riziculture', 'Culture du riz dans les rizieres (vary)', 'AGRICULTURE_VIVRIERE'),
    ('Maraichage', 'Culture des legumes et plantes potageres', 'AGRICULTURE_VIVRIERE'),
    ('Culture de manioc', 'Production de manioc et tubercules', 'AGRICULTURE_VIVRIERE'),
    ('Cereales', 'Mais, sorgho, ble', 'AGRICULTURE_VIVRIERE'),
    ('Vanille', 'Production et collecte de vanille', 'AGRICULTURE_COMMERCIALE'),
    ('Girofle', 'Production et collecte de girofle', 'AGRICULTURE_COMMERCIALE'),
    ('Cafe', 'Cafeiculture', 'AGRICULTURE_COMMERCIALE'),
    ('Litchi', 'Production de litchi pour export', 'AGRICULTURE_COMMERCIALE'),
    ('Cacao', 'Cacaoyere', 'AGRICULTURE_COMMERCIALE'),
    ('Elevage bovin', 'Zebus et bovins', 'ELEVAGE'),
    ('Aviculture', 'Poulets, pintades, canards', 'ELEVAGE'),
    ('Apiculture', 'Production de miel', 'ELEVAGE'),
    ('Peche maritime', 'Peche en mer et sur la cete', 'PECHE'),
    ('Pisciculture', 'Aquaculture en eau douce', 'PECHE'),
    ('Floriculture', 'Production de fleurs', 'HORTICULTURE'),
    ('Arboriculture fruitiere', 'Arbres fruitiers tropicaux', 'HORTICULTURE');
