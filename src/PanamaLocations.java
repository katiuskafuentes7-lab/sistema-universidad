import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Geographic names adapted from Eric Lucero Gonzalez's Panama Political Division dataset (MIT).
final class PanamaLocations {
    private static final String DATA = """
            Bocas del Toro|Bocas del Toro|Bocas Del Toro
            Bocas del Toro|Bocas del Toro|Bastimentos
            Bocas del Toro|Bocas del Toro|Punta Laurel
            Bocas del Toro|Bocas del Toro|Tierra Oscura
            Bocas del Toro|Bocas del Toro|Bocas Del Drago
            Bocas del Toro|Bocas del Toro|San Cristobal
            Bocas del Toro|Changuinola|Changuinola
            Bocas del Toro|Changuinola|Guabito
            Bocas del Toro|Changuinola|El Teribe
            Bocas del Toro|Changuinola|El Empalme
            Bocas del Toro|Changuinola|Las Tablas
            Bocas del Toro|Changuinola|Cochigró
            Bocas del Toro|Changuinola|La Gloria
            Bocas del Toro|Changuinola|Las Delicias
            Bocas del Toro|Changuinola|Barriada 4 De Abril
            Bocas del Toro|Changuinola|El Silencio
            Bocas del Toro|Changuinola|Finca 6
            Bocas del Toro|Changuinola|Finca 30
            Bocas del Toro|Changuinola|Finca 60
            Bocas del Toro|Changuinola|Barranco Adentro
            Bocas del Toro|Changuinola|Finca 4
            Bocas del Toro|Changuinola|Finca 12
            Bocas del Toro|Changuinola|Finca 51
            Bocas del Toro|Changuinola|Finca 66
            Bocas del Toro|Changuinola|La Mesa
            Bocas del Toro|Chiriquí Grande|Chiriquí Grande
            Bocas del Toro|Chiriquí Grande|Miramar
            Bocas del Toro|Chiriquí Grande|Punta Peña
            Bocas del Toro|Chiriquí Grande|Punta Robalo
            Bocas del Toro|Chiriquí Grande|Rambala
            Bocas del Toro|Chiriquí Grande|Bajo Cedro
            Bocas del Toro|Almirante|Almirante
            Bocas del Toro|Almirante|Barrio Francés
            Bocas del Toro|Almirante|Barriada Guaymí
            Bocas del Toro|Almirante|Nance De Risco
            Bocas del Toro|Almirante|Valle De Agua Arriba
            Bocas del Toro|Almirante|Valle Del Risco
            Bocas del Toro|Almirante|Bajo Culubre
            Bocas del Toro|Almirante|Cauchero
            Bocas del Toro|Almirante|Ceiba
            Bocas del Toro|Almirante|Miraflores
            Coclé|Aguadulce|Aguadulce
            Coclé|Aguadulce|El Cristo
            Coclé|Aguadulce|El Roble
            Coclé|Aguadulce|Pocrí
            Coclé|Aguadulce|Barrios Unidos
            Coclé|Aguadulce|Pueblos Unidos
            Coclé|Aguadulce|Virgen Del Carmen
            Coclé|Aguadulce|El Hato De San Juan De Dios
            Coclé|Antón|Antón
            Coclé|Antón|Cabuya
            Coclé|Antón|El Chirú
            Coclé|Antón|El Retiro
            Coclé|Antón|El Valle
            Coclé|Antón|Juan Díaz
            Coclé|Antón|Río Hato
            Coclé|Antón|San Juan De Dios
            Coclé|Antón|Santa Rita
            Coclé|Antón|Caballero
            Coclé|La Pintada|La Pintada
            Coclé|La Pintada|El Harino
            Coclé|La Pintada|El Potrero
            Coclé|La Pintada|Llano Grande
            Coclé|La Pintada|Piedras Gordas
            Coclé|La Pintada|Las Lomas
            Coclé|La Pintada|Llano Norte
            Coclé|Natá|Natá
            Coclé|Natá|Capellanía
            Coclé|Natá|El Caño
            Coclé|Natá|Guzmán
            Coclé|Natá|Las Huacas
            Coclé|Natá|Toza
            Coclé|Natá|Villarreal
            Coclé|Olá|Olá
            Coclé|Olá|El Copé
            Coclé|Olá|El Palmar
            Coclé|Olá|El Picacho
            Coclé|Olá|La Pava
            Coclé|Penonomé|Penonomé
            Coclé|Penonomé|Cañaveral
            Coclé|Penonomé|Coclé
            Coclé|Penonomé|Chiguirí Arriba
            Coclé|Penonomé|El Coco
            Coclé|Penonomé|Pajonal
            Coclé|Penonomé|Río Grande
            Coclé|Penonomé|Río Indio
            Coclé|Penonomé|Toabré
            Coclé|Penonomé|Tulú
            Coclé|Penonomé|Boca De Tucué
            Coclé|Penonomé|Candelario Ovalle
            Coclé|Penonomé|General Victoriano Lorenzo
            Coclé|Penonomé|Las Minas
            Coclé|Penonomé|Riecito
            Coclé|Penonomé|San Miguel
            Colón|Colón|Barrio Norte
            Colón|Colón|Barrio Sur
            Colón|Colón|Buena Vista
            Colón|Colón|Cativá
            Colón|Colón|Ciricito
            Colón|Colón|Cristóbal
            Colón|Colón|Escobal
            Colón|Colón|Limón
            Colón|Colón|Nueva Providencia
            Colón|Colón|Puerto Pilón
            Colón|Colón|Sabanitas
            Colón|Colón|Salamanca
            Colón|Colón|San Juan
            Colón|Colón|Santa Rosa
            Colón|Colón|Cristóbal Este
            Colón|Chagres|Nuevo Chagres
            Colón|Chagres|Achiote
            Colón|Chagres|El Guabo
            Colón|Chagres|La Encantada
            Colón|Chagres|Palmas Bellas
            Colón|Chagres|Piña
            Colón|Chagres|Salud
            Colón|Donoso|Miguel De La Borda
            Colón|Donoso|Coclé Del Norte
            Colón|Donoso|El Guásimo
            Colón|Donoso|Gobea
            Colón|Donoso|Río Indio
            Colón|Portobelo|Portobelo
            Colón|Portobelo|Cacique
            Colón|Portobelo|Puerto Lindo O Garrote
            Colón|Portobelo|Isla Grande
            Colón|Portobelo|María Chiquita
            Colón|Santa Isabel|Palenque
            Colón|Santa Isabel|Cuango
            Colón|Santa Isabel|Miramar
            Colón|Santa Isabel|Nombre De Dios
            Colón|Santa Isabel|Palmira
            Colón|Santa Isabel|Playa Chiquita
            Colón|Santa Isabel|Santa Isabel
            Colón|Santa Isabel|Viento Frío
            Colón|Omar Torrijos Herrera|San José Del General
            Colón|Omar Torrijos Herrera|Nueva Esperanza
            Colón|Omar Torrijos Herrera|San Juan De Turbe
            Chiriquí|Alanje|Alanje
            Chiriquí|Alanje|Divalá
            Chiriquí|Alanje|El Tejar
            Chiriquí|Alanje|Guarumal
            Chiriquí|Alanje|Palo Grande
            Chiriquí|Alanje|Querévalo
            Chiriquí|Alanje|Santo Tomás
            Chiriquí|Alanje|Canta Gallo
            Chiriquí|Alanje|Nuevo México
            Chiriquí|Barú|Puerto Armuelles
            Chiriquí|Barú|Limones
            Chiriquí|Barú|Progreso
            Chiriquí|Barú|Baco
            Chiriquí|Barú|Rodolfo Aguilar Delgado
            Chiriquí|Barú|El Palmar
            Chiriquí|Barú|Manaca
            Chiriquí|Boquerón|Boquerón
            Chiriquí|Boquerón|Bágala
            Chiriquí|Boquerón|Cordillera
            Chiriquí|Boquerón|Guabal
            Chiriquí|Boquerón|Guayabal
            Chiriquí|Boquerón|Paraíso
            Chiriquí|Boquerón|Pedregal
            Chiriquí|Boquerón|Tijeras
            Chiriquí|Boquete|Bajo Boquete
            Chiriquí|Boquete|Caldera
            Chiriquí|Boquete|Palmira
            Chiriquí|Boquete|Alto Boquete
            Chiriquí|Boquete|Jaramillo
            Chiriquí|Boquete|Los Naranjos
            Chiriquí|Bugaba|La Concepción
            Chiriquí|Bugaba|Aserrío De Gariché
            Chiriquí|Bugaba|Bugaba
            Chiriquí|Bugaba|Gómez
            Chiriquí|Bugaba|La Estrella
            Chiriquí|Bugaba|San Andrés
            Chiriquí|Bugaba|Santa Marta
            Chiriquí|Bugaba|Santa Rosa
            Chiriquí|Bugaba|Santo Domingo
            Chiriquí|Bugaba|Sortová
            Chiriquí|Bugaba|El Bongo
            Chiriquí|Bugaba|Solano
            Chiriquí|Bugaba|San Isidro
            Chiriquí|David|David
            Chiriquí|David|Bijagual
            Chiriquí|David|Cochea
            Chiriquí|David|Chiriquí
            Chiriquí|David|Guacá
            Chiriquí|David|Las Lomas
            Chiriquí|David|Pedregal
            Chiriquí|David|San Carlos
            Chiriquí|David|San Pablo Nuevo
            Chiriquí|David|San Pablo Viejo
            Chiriquí|David|David Este
            Chiriquí|David|David Sur
            Chiriquí|Dolega|Dolega
            Chiriquí|Dolega|Dos Ríos
            Chiriquí|Dolega|Los Anastacios
            Chiriquí|Dolega|Potrerillos
            Chiriquí|Dolega|Potrerillos Abajo
            Chiriquí|Dolega|Rovira
            Chiriquí|Dolega|Tinajas
            Chiriquí|Dolega|Los Algarrobos
            Chiriquí|Gualaca|Gualaca
            Chiriquí|Gualaca|Hornito
            Chiriquí|Gualaca|Los ángeles
            Chiriquí|Gualaca|Paja De Sombrero
            Chiriquí|Gualaca|Rincón
            Chiriquí|Remedios|Remedios
            Chiriquí|Remedios|El Nancito
            Chiriquí|Remedios|El Porvenir
            Chiriquí|Remedios|El Puerto
            Chiriquí|Remedios|Santa Lucia
            Chiriquí|Renacimiento|Río Sereno
            Chiriquí|Renacimiento|Breñon
            Chiriquí|Renacimiento|Cañas Gordas
            Chiriquí|Renacimiento|Monte Lirio
            Chiriquí|Renacimiento|Plaza Caisán
            Chiriquí|Renacimiento|Santa Cruz
            Chiriquí|Renacimiento|Dominical
            Chiriquí|Renacimiento|Santa Clara
            Chiriquí|San Félix|Las Lajas
            Chiriquí|San Félix|Juay
            Chiriquí|San Félix|Lajas Adentro
            Chiriquí|San Félix|San Felix
            Chiriquí|San Félix|Santa Cruz
            Chiriquí|San Lorenzo|Horconcitos
            Chiriquí|San Lorenzo|Boca Chica
            Chiriquí|San Lorenzo|Boca Del Monte
            Chiriquí|San Lorenzo|San Juan
            Chiriquí|San Lorenzo|San Lorenzo
            Chiriquí|Tolé|Tolé
            Chiriquí|Tolé|Bella Vista
            Chiriquí|Tolé|Cerro Viejo
            Chiriquí|Tolé|El Cristo
            Chiriquí|Tolé|Justo Fidel Palacios
            Chiriquí|Tolé|Lajas De Tolé
            Chiriquí|Tolé|Potrero De Caña
            Chiriquí|Tolé|Quebrada De Piedra
            Chiriquí|Tolé|Veladero
            Chiriquí|Tierras Altas|Volcán
            Chiriquí|Tierras Altas|Cerro Punta
            Chiriquí|Tierras Altas|Cuesta De Piedra
            Chiriquí|Tierras Altas|Nueva California
            Chiriquí|Tierras Altas|Paso Ancho
            Darién|Chepigana|La Palma
            Darién|Chepigana|Camogantí
            Darién|Chepigana|Chepigana
            Darién|Chepigana|Garachiné
            Darién|Chepigana|Jaqué
            Darién|Chepigana|Puerto Piña
            Darién|Chepigana|Sambú
            Darién|Chepigana|Setegantí
            Darién|Chepigana|Taimatí
            Darién|Chepigana|Tucutí
            Darién|Pinogana|El Real De Santa María
            Darién|Pinogana|Boca De Cupé
            Darién|Pinogana|Paya
            Darién|Pinogana|Pinogana
            Darién|Pinogana|Púcuro
            Darién|Pinogana|Yape
            Darién|Pinogana|Yaviza
            Darién|Pinogana|Metetí
            Darién|Pinogana|Comarca Kuna De Wargandí
            Darién|Santa Fe|Río Congo
            Darién|Santa Fe|Río Iglesias
            Darién|Santa Fe|Agua Fría
            Darién|Santa Fe|Cucunatí
            Darién|Santa Fe|Río Congo Arriba
            Darién|Santa Fe|Santa Fe
            Darién|Santa Fe|Zapallal
            Herrera|Chitré|Chitré
            Herrera|Chitré|La Arena
            Herrera|Chitré|Monagrillo
            Herrera|Chitré|Llano Bonito
            Herrera|Chitré|San Juan Bautista
            Herrera|Las Minas|Las Minas
            Herrera|Las Minas|Chepo
            Herrera|Las Minas|Chumical
            Herrera|Las Minas|El Toro
            Herrera|Las Minas|Leones
            Herrera|Las Minas|Quebrada Del Rosario
            Herrera|Las Minas|Quebrada El Ciprián
            Herrera|Los Pozos|Los Pozos
            Herrera|Los Pozos|Capurí
            Herrera|Los Pozos|El Calabacito
            Herrera|Los Pozos|El Cedro
            Herrera|Los Pozos|La Arena
            Herrera|Los Pozos|La Pitaloza
            Herrera|Los Pozos|Los Cerritos
            Herrera|Los Pozos|Los Cerros De Paja
            Herrera|Los Pozos|Las Llanas
            Herrera|Ocú|Ocú
            Herrera|Ocú|Cerro Largo
            Herrera|Ocú|Los Llanos
            Herrera|Ocú|Llano Grande
            Herrera|Ocú|Peñas Chatas
            Herrera|Ocú|El Tijera
            Herrera|Ocú|Menchaca
            Herrera|Ocú|Entradero Del Castillo
            Herrera|Parita|Parita
            Herrera|Parita|Cabuya
            Herrera|Parita|Los Castillos
            Herrera|Parita|Llano De La Cruz
            Herrera|Parita|París
            Herrera|Parita|Portobelillo
            Herrera|Parita|Potuga
            Herrera|Pesé|Pesé
            Herrera|Pesé|Las Cabras
            Herrera|Pesé|El Pájaro
            Herrera|Pesé|El Barrero
            Herrera|Pesé|El Pedregoso
            Herrera|Pesé|El Ciruelo
            Herrera|Pesé|Sabanagrande
            Herrera|Pesé|Rincón Hondo
            Herrera|Santa María|Santa María
            Herrera|Santa María|Chupampa
            Herrera|Santa María|El Rincón
            Herrera|Santa María|El Limón
            Herrera|Santa María|Los Canelos
            Los Santos|Guararé|Guararé
            Los Santos|Guararé|El Espinal
            Los Santos|Guararé|El Macano
            Los Santos|Guararé|Guararé Arriba
            Los Santos|Guararé|La Enea
            Los Santos|Guararé|La Pasera
            Los Santos|Guararé|Las Trancas
            Los Santos|Guararé|Llano Abajo
            Los Santos|Guararé|El Hato
            Los Santos|Guararé|Perales
            Los Santos|Las Tablas|Las Tablas
            Los Santos|Las Tablas|Bajo Corral
            Los Santos|Las Tablas|Bayano
            Los Santos|Las Tablas|El Carate
            Los Santos|Las Tablas|El Cocal
            Los Santos|Las Tablas|El Manantial
            Los Santos|Las Tablas|El Muñoz
            Los Santos|Las Tablas|El Pedregoso
            Los Santos|Las Tablas|La Laja
            Los Santos|Las Tablas|La Miel
            Los Santos|Las Tablas|La Palma
            Los Santos|Las Tablas|La Tiza
            Los Santos|Las Tablas|Las Palmitas
            Los Santos|Las Tablas|Las Tablas Abajo
            Los Santos|Las Tablas|Nuario
            Los Santos|Las Tablas|Palmira
            Los Santos|Las Tablas|Peña Blanca
            Los Santos|Las Tablas|Río Hondo
            Los Santos|Las Tablas|San José
            Los Santos|Las Tablas:|San Miguel
            Los Santos|Las Tablas:|Santo Domingo
            Los Santos|Las Tablas:|Sesteadero
            Los Santos|Las Tablas:|Valle Rico
            Los Santos|Las Tablas:|Vallerriquito
            Los Santos|Los Santos|La Villa De Los Santos
            Los Santos|Los Santos|El Guásimo
            Los Santos|Los Santos|La Colorada
            Los Santos|Los Santos|La Espigadilla
            Los Santos|Los Santos|Las Cruces
            Los Santos|Los Santos|Las Guabas
            Los Santos|Los Santos|Los Angeles
            Los Santos|Los Santos|Los Olivos
            Los Santos|Los Santos|Llano Largo
            Los Santos|Los Santos|Sabanagrande
            Los Santos|Los Santos|Santa Ana
            Los Santos|Los Santos|Tres Quebradas
            Los Santos|Los Santos|Agua Buena
            Los Santos|Los Santos|Villa Lourdes
            Los Santos|Los Santos|El Ejido
            Los Santos|Macaracas|Macaracas
            Los Santos|Macaracas|Bahía Honda
            Los Santos|Macaracas|Bajos De Güera
            Los Santos|Macaracas|Corozal
            Los Santos|Macaracas|Chupá
            Los Santos|Macaracas|El Cedro
            Los Santos|Macaracas|Espino Amarillo
            Los Santos|Macaracas|La Mesa
            Los Santos|Macaracas|Las Palmas
            Los Santos|Macaracas|Llano De Piedra
            Los Santos|Macaracas|Mogollón
            Los Santos|Pedasí|Pedasí
            Los Santos|Pedasí|Los Asientos
            Los Santos|Pedasí|Mariabé
            Los Santos|Pedasí|Purio
            Los Santos|Pedasí|Oria Arriba
            Los Santos|Pocrí|Pocrí
            Los Santos|Pocrí|El Cañafístulo
            Los Santos|Pocrí|Lajamina
            Los Santos|Pocrí|Paraíso
            Los Santos|Pocrí|Paritilla
            Los Santos|Tonosí|Tonosí
            Los Santos|Tonosí|Altos De Güera
            Los Santos|Tonosí|Cañas
            Los Santos|Tonosí|El Bebedero
            Los Santos|Tonosí|El Cacao
            Los Santos|Tonosí|El Cortezo
            Los Santos|Tonosí|Flores
            Los Santos|Tonosí|Guánico
            Los Santos|Tonosí|La Tronosa
            Los Santos|Tonosí|Cambutal
            Los Santos|Tonosí|Isla De Cañas
            Panamá|Balboa|San Miguel
            Panamá|Balboa|La Ensenada
            Panamá|Balboa|La Esmeralda
            Panamá|Balboa|La Guinea
            Panamá|Balboa|Pedro González
            Panamá|Balboa|Saboga
            Panamá|Chepo|Chepo
            Panamá|Chepo|Cañita
            Panamá|Chepo|Chepillo
            Panamá|Chepo|El Llano
            Panamá|Chepo|Las Margaritas
            Panamá|Chepo|Santa Cruz De Chinina
            Panamá|Chepo|Comarca Kuna De Madungandí
            Panamá|Chepo|Tortí
            Panamá|Chimán|Chimán
            Panamá|Chimán|Brujas
            Panamá|Chimán|Gonzalo Vásquez
            Panamá|Chimán|Pásiga
            Panamá|Chimán|Unión Santeña
            Panamá|Panamá|San Felipe
            Panamá|Panamá|El Chorrillo
            Panamá|Panamá|Santa Ana
            Panamá|Panamá|La Exposición O Calidonia
            Panamá|Panamá|Curundú
            Panamá|Panamá|Betania
            Panamá|Panamá|Bella Vista
            Panamá|Panamá|Pueblo Nuevo
            Panamá|Panamá|San Francisco
            Panamá|Panamá|Parque Lefevre
            Panamá|Panamá|Río Abajo
            Panamá|Panamá|Juan Díaz
            Panamá|Panamá|Pedregal
            Panamá|Panamá|Ancón
            Panamá|Panamá|Chilibre
            Panamá|Panamá|Las Cumbres
            Panamá|Panamá|Pacora
            Panamá|Panamá|San Martín
            Panamá|Panamá|Tocumen
            Panamá|Panamá|Las Mañanitas
            Panamá|Panamá|24 De Diciembre
            Panamá|Panamá|Alcalde Díaz
            Panamá|Panamá|Ernesto Córdoba Campos
            Panamá|Panamá|Caimitillo
            Panamá|Panamá|Las Garzas
            Panamá|Panamá|Don Bosco
            Panamá|San Miguelito|Amelia Denis De Icaza
            Panamá|San Miguelito|Belisario Porras
            Panamá|San Miguelito|José Domingo Espinar
            Panamá|San Miguelito|Mateo Iturralde
            Panamá|San Miguelito|Victoriano Lorenzo
            Panamá|San Miguelito|Arnulfo Arias
            Panamá|San Miguelito|Belisario Frías
            Panamá|San Miguelito|Omar Torrijos
            Panamá|San Miguelito|Rufina Alfaro
            Panamá|Taboga|Taboga
            Panamá|Taboga|Otoque Occidente
            Panamá|Taboga|Otoque Oriente
            Panamá Oeste|Arraiján|Arraiján
            Panamá Oeste|Arraiján|Juan Demóstenes Arosemena
            Panamá Oeste|Arraiján|Nuevo Emperador
            Panamá Oeste|Arraiján|Santa Clara
            Panamá Oeste|Arraiján|Veracruz
            Panamá Oeste|Arraiján|Vista Alegre
            Panamá Oeste|Arraiján|Burunga
            Panamá Oeste|Arraiján|Cerro Silvestre
            Panamá Oeste|Arraiján|Vacamonte
            Panamá Oeste|Capira|Capira
            Panamá Oeste|Capira|Caimito
            Panamá Oeste|Capira|Campana
            Panamá Oeste|Capira|Cermeño
            Panamá Oeste|Capira|Cirí De Los Sotos
            Panamá Oeste|Capira|Cirí Grande
            Panamá Oeste|Capira|El Cacao
            Panamá Oeste|Capira|La Trinidad
            Panamá Oeste|Capira|Las Ollas Arriba
            Panamá Oeste|Capira|Lídice
            Panamá Oeste|Capira|Villa Carmen
            Panamá Oeste|Capira|Villa Rosario
            Panamá Oeste|Capira|Santa Rosa
            Panamá Oeste|Chame|Chame
            Panamá Oeste|Chame|Bejuco
            Panamá Oeste|Chame|Buenos Aires
            Panamá Oeste|Chame|Cabuya
            Panamá Oeste|Chame|Chicá
            Panamá Oeste|Chame|El Líbano
            Panamá Oeste|Chame|Las Lajas
            Panamá Oeste|Chame|Nueva Gorgona
            Panamá Oeste|Chame|Punta Chame
            Panamá Oeste|Chame|Sajalices
            Panamá Oeste|Chame|Sorá
            Panamá Oeste|La Chorrera|Barrio Balboa
            Panamá Oeste|La Chorrera|Barrio Colón
            Panamá Oeste|La Chorrera|Amador
            Panamá Oeste|La Chorrera|Arosemena
            Panamá Oeste|La Chorrera|El Arado
            Panamá Oeste|La Chorrera|El Coco
            Panamá Oeste|La Chorrera|Feuillet
            Panamá Oeste|La Chorrera|Guadalupe
            Panamá Oeste|La Chorrera|Herrera
            Panamá Oeste|La Chorrera|Hurtado
            Panamá Oeste|La Chorrera|Iturralde
            Panamá Oeste|La Chorrera|La Represa
            Panamá Oeste|La Chorrera|Los Díaz
            Panamá Oeste|La Chorrera|Mendoza
            Panamá Oeste|La Chorrera|Obaldía
            Panamá Oeste|La Chorrera|Playa Leona
            Panamá Oeste|La Chorrera|Puerto Caimito
            Panamá Oeste|La Chorrera|Santa Rita
            Panamá Oeste|San Carlos|San Carlos
            Panamá Oeste|San Carlos|El Espino
            Panamá Oeste|San Carlos|El Higo
            Panamá Oeste|San Carlos|Guayabito
            Panamá Oeste|San Carlos|La Ermita
            Panamá Oeste|San Carlos|La Laguna
            Panamá Oeste|San Carlos|Las Uvas
            Panamá Oeste|San Carlos|Los Llanitos
            Panamá Oeste|San Carlos|San José
            Veraguas|Atalaya|Atalaya
            Veraguas|Atalaya|El Barrito
            Veraguas|Atalaya|La Montañuela
            Veraguas|Atalaya|La Carrillo
            Veraguas|Atalaya|San Antonio
            Veraguas|Calobre|Calobre
            Veraguas|Calobre|Barnizal
            Veraguas|Calobre|Chitra
            Veraguas|Calobre|El Cocla
            Veraguas|Calobre|El Potrero
            Veraguas|Calobre|La Laguna
            Veraguas|Calobre|La Raya De Calobre
            Veraguas|Calobre|La Tetilla
            Veraguas|Calobre|La Yeguada
            Veraguas|Calobre|Las Guías
            Veraguas|Calobre|Monjarás
            Veraguas|Calobre|San José
            Veraguas|Cañazas|Cañazas
            Veraguas|Cañazas|Cerro De Plata
            Veraguas|Cañazas|El Picador
            Veraguas|Cañazas|Los Valles
            Veraguas|Cañazas|San José
            Veraguas|Cañazas|San Marcelo
            Veraguas|Cañazas|El Aromillo
            Veraguas|Cañazas|Las Cruces
            Veraguas|La Mesa|La Mesa
            Veraguas|La Mesa|Bisvalles
            Veraguas|La Mesa|Boró
            Veraguas|La Mesa|Llano Grande
            Veraguas|La Mesa|San Bartolo
            Veraguas|La Mesa|Los Milagros
            Veraguas|La Mesa|El Higo
            Veraguas|Las Palmas|Las Palmas
            Veraguas|Las Palmas|Cerro De Casa
            Veraguas|Las Palmas|Corozal
            Veraguas|Las Palmas|El María
            Veraguas|Las Palmas|El Prado
            Veraguas|Las Palmas|El Rincón
            Veraguas|Las Palmas|Lolá
            Veraguas|Las Palmas|Pixvae
            Veraguas|Las Palmas|Puerto Vidal
            Veraguas|Las Palmas|San Martín De Porres
            Veraguas|Las Palmas|Viguí
            Veraguas|Las Palmas|Zapotillo
            Veraguas|Las Palmas|Manuel E. Amador Terrero
            Veraguas|Montijo|Montijo
            Veraguas|Montijo|Gobernadora
            Veraguas|Montijo|La Garceana
            Veraguas|Montijo|Leones
            Veraguas|Montijo|Pilón
            Veraguas|Montijo|Cébaco
            Veraguas|Montijo|Costa Hermosa
            Veraguas|Montijo|Unión Del Norte
            Veraguas|Río De Jesús|Río De Jesús
            Veraguas|Río De Jesús|Las Huacas
            Veraguas|Río De Jesús|Los Castillos
            Veraguas|Río De Jesús|Utira
            Veraguas|Río De Jesús|Catorce De Noviembre
            Veraguas|San Francisco|San Francisco
            Veraguas|San Francisco|Corral Falso
            Veraguas|San Francisco|Los Hatillos
            Veraguas|San Francisco|Remance
            Veraguas|San Francisco|San Juan
            Veraguas|San Francisco|San José
            Veraguas|Santa Fe|Santa Fe
            Veraguas|Santa Fe|Calovébora
            Veraguas|Santa Fe|El Alto
            Veraguas|Santa Fe|El Cuay
            Veraguas|Santa Fe|El Pantano
            Veraguas|Santa Fe|Gatú O Gatucito
            Veraguas|Santa Fe|Río Luis
            Veraguas|Santa Fe|Rubén Cantú
            Veraguas|Santiago|Santiago
            Veraguas|Santiago|La Colorada
            Veraguas|Santiago|La Peña
            Veraguas|Santiago|La Raya De Santa María
            Veraguas|Santiago|Ponuga
            Veraguas|Santiago|San Pedro Del Espino
            Veraguas|Santiago|Canto Del Llano
            Veraguas|Santiago|Los Algarrobos
            Veraguas|Santiago|Carlos Santana ávila
            Veraguas|Santiago|Edwin Fábrega
            Veraguas|Santiago|San Martín De Porres
            Veraguas|Santiago|Urracá
            Veraguas|Santiago|Rodrigo Luque
            Veraguas|Santiago|Nuevo Santiago
            Veraguas|Santiago|Santiago Este
            Veraguas|Santiago|Santiago Sur
            Veraguas|Soná|Soná
            Veraguas|Soná|Bahía Honda
            Veraguas|Soná|Calidonia
            Veraguas|Soná|Cativé
            Veraguas|Soná|El Marañón
            Veraguas|Soná|Guarumal
            Veraguas|Soná|La Soledad
            Veraguas|Soná|Quebrada De Oro
            Veraguas|Soná|Río Grande
            Veraguas|Soná|Rodeo Viejo
            Veraguas|Soná|Hicaco
            Veraguas|Soná|La Trinchera
            Veraguas|Mariato|Llano De Catival O Mariato
            Veraguas|Mariato|Arenas
            Veraguas|Mariato|El Cacao
            Veraguas|Mariato|Quebro
            Veraguas|Mariato|Tebario
            Comarca Guna Yala|Comarca Kuna Yala|Narganá
            Comarca Guna Yala|Comarca Kuna Yala|Ailigandí
            Comarca Guna Yala|Comarca Kuna Yala|Puerto Obaldía
            Comarca Guna Yala|Comarca Kuna Yala|Tubualá
            Comarca Emberá-Wounaan|Cémaco|Cirilo Guaynora
            Comarca Emberá-Wounaan|Cémaco|Lajas Blancas
            Comarca Emberá-Wounaan|Cémaco|Manuel Ortega
            Comarca Emberá-Wounaan|Sambú|Río Sábalo
            Comarca Emberá-Wounaan|Sambú|Jingurudó
            Comarca Ngäbe-Buglé|Besiko|Soloy
            Comarca Ngäbe-Buglé|Besiko|Boca De Balsa
            Comarca Ngäbe-Buglé|Besiko|Camarón Arriba
            Comarca Ngäbe-Buglé|Besiko|Cerro Banco
            Comarca Ngäbe-Buglé|Besiko|Cerro De Patena
            Comarca Ngäbe-Buglé|Besiko|Emplanada De Chorcha
            Comarca Ngäbe-Buglé|Besiko|Nämnoni
            Comarca Ngäbe-Buglé|Besiko|Niba
            Comarca Ngäbe-Buglé|Mironó|Hato Pilón
            Comarca Ngäbe-Buglé|Mironó|Cascabel
            Comarca Ngäbe-Buglé|Mironó|Hato Corotú
            Comarca Ngäbe-Buglé|Mironó|Hato Culantro
            Comarca Ngäbe-Buglé|Mironó|Hato Jobo
            Comarca Ngäbe-Buglé|Mironó|Hato Julí
            Comarca Ngäbe-Buglé|Mironó|Quebrada De Loro
            Comarca Ngäbe-Buglé|Mironó|Salto Dupí
            Comarca Ngäbe-Buglé|Müna|Chichica
            Comarca Ngäbe-Buglé|Müna|Alto Caballero
            Comarca Ngäbe-Buglé|Müna|Bakama
            Comarca Ngäbe-Buglé|Müna|Cerro Caña
            Comarca Ngäbe-Buglé|Müna|Cerro Puerco
            Comarca Ngäbe-Buglé|Müna|Krüa
            Comarca Ngäbe-Buglé|Müna|Maraca
            Comarca Ngäbe-Buglé|Müna|Nibra
            Comarca Ngäbe-Buglé|Müna|Peña Blanca
            Comarca Ngäbe-Buglé|Müna|Roka
            Comarca Ngäbe-Buglé|Müna|Sitio Prado
            Comarca Ngäbe-Buglé|Müna|ümani
            Comarca Ngäbe-Buglé|Müna|Dikeri
            Comarca Ngäbe-Buglé|Müna|Diko
            Comarca Ngäbe-Buglé|Müna|Kikari
            Comarca Ngäbe-Buglé|Müna|Mreeni
            Comarca Ngäbe-Buglé|Nole Duima|Cerro Iglesias
            Comarca Ngäbe-Buglé|Nole Duima|Hato Chamí
            Comarca Ngäbe-Buglé|Nole Duima|Jädaberi
            Comarca Ngäbe-Buglé|Nole Duima|Lajero
            Comarca Ngäbe-Buglé|Nole Duima|Susama
            Comarca Ngäbe-Buglé|ñürüm|Buenos Aires
            Comarca Ngäbe-Buglé|ñürüm|Agua De Salud
            Comarca Ngäbe-Buglé|ñürüm|Alto De Jesús
            Comarca Ngäbe-Buglé|ñürüm|Cerro Pelado
            Comarca Ngäbe-Buglé|ñürüm|El Bale
            Comarca Ngäbe-Buglé|ñürüm|El Paredón
            Comarca Ngäbe-Buglé|ñürüm|El Piro
            Comarca Ngäbe-Buglé|ñürüm|Guayabito
            Comarca Ngäbe-Buglé|ñürüm|Güibale
            Comarca Ngäbe-Buglé|ñürüm|El Peñon
            Comarca Ngäbe-Buglé|ñürüm|El Piro No.2
            Comarca Ngäbe-Buglé|Kankintú|Bisira
            Comarca Ngäbe-Buglé|Kankintú|Guoroni
            Comarca Ngäbe-Buglé|Kankintú|Kankintú
            Comarca Ngäbe-Buglé|Kankintú|Mününi
            Comarca Ngäbe-Buglé|Kankintú|Piedra Roja
            Comarca Ngäbe-Buglé|Kankintú|Calante
            Comarca Ngäbe-Buglé|Kankintú|Tolote
            Comarca Ngäbe-Buglé|Kusapín|Kusapín
            Comarca Ngäbe-Buglé|Kusapín|Bahía Azul
            Comarca Ngäbe-Buglé|Kusapín|Río Chiriquí
            Comarca Ngäbe-Buglé|Kusapín|Tobobe
            Comarca Ngäbe-Buglé|Kusapín|Cañaveral
            Comarca Ngäbe-Buglé|Jirondai|Samboa
            Comarca Ngäbe-Buglé|Jirondai|Bürí
            Comarca Ngäbe-Buglé|Jirondai|Guariviara
            Comarca Ngäbe-Buglé|Jirondai|Man Creek
            Comarca Ngäbe-Buglé|Jirondai|Tuwai
            Comarca Ngäbe-Buglé|Santa Catalina O Calovébora|Santa Catalina O Calovébora
            Comarca Ngäbe-Buglé|Santa Catalina O Calovébora|Alto Bilingüe
            Comarca Ngäbe-Buglé|Santa Catalina O Calovébora|Loma Yuca
            Comarca Ngäbe-Buglé|Santa Catalina O Calovébora|San Pedrito
            Comarca Ngäbe-Buglé|Santa Catalina O Calovébora|Valle Bonito
            """;
    private static final Map<String, Map<String, List<String>>> LOCATIONS = load();

    private PanamaLocations() {
    }

    static String[] districts(String province) {
        Map<String, List<String>> districts = LOCATIONS.get(province);
        return options(districts == null ? List.of() : new ArrayList<>(districts.keySet()));
    }

    static String[] corregimientos(String province, String district) {
        Map<String, List<String>> districts = LOCATIONS.get(province);
        return options(districts == null ? List.of() : districts.getOrDefault(district, List.of()));
    }

    private static String[] options(List<String> values) {
        String[] options = new String[values.size() + 1];
        options[0] = "";
        for (int i = 0; i < values.size(); i++) {
            options[i + 1] = values.get(i);
        }
        return options;
    }

    private static Map<String, Map<String, List<String>>> load() {
        Map<String, Map<String, List<String>>> locations = new LinkedHashMap<>();
        for (String row : DATA.split("\\R")) {
            String[] values = row.split("\\|", 3);
            if (values.length != 3) {
                throw new IllegalStateException("Invalid Panama location data row");
            }
            locations.computeIfAbsent(values[0], ignored -> new LinkedHashMap<>())
                    .computeIfAbsent(values[1], ignored -> new ArrayList<>())
                    .add(values[2]);
        }
        return locations;
    }
}
