-- Ideias Midias seed for PostgreSQL
-- Fixed version: includes created_at and updated_at for NOT NULL audit columns
-- Save this file as UTF-8

BEGIN;

INSERT INTO site_settings (
    created_at,
    updated_at,
    company_name_pt,
    company_name_en,
    short_intro_pt,
    short_intro_en,
    hero_title_pt,
    hero_title_en,
    hero_subtitle_pt,
    hero_subtitle_en,
    logo_url,
    hero_background_type,
    hero_background_url,
    company_video_url,
    address_pt,
    address_en,
    map_embed_url,
    location_lat,
    location_lng
)
SELECT
    NOW(),
    NOW(),
    'Ideias Mídias',
    'Ideias Midias',
    'Publicidade & Impressão',
    'Advertising & Printing',
    'Ideias Mídias',
    'Ideias Midias',
    'Publicidade & Impressão',
    'Advertising & Printing',
    NULL,
    'IMAGE',
    NULL,
    NULL,
    'Av. 21 de Janeiro, Morro Bento - junto a Casa do Koly Vila. Gamek à direita, depois do Quintalão das Chanas, segunda entrada à direita (fábrica).',
    '21 de Janeiro Avenue, Morro Bento - near Casa do Koly Vila. Turn right at Gamek, after Quintalão das Chanas, second entrance on the right (factory).',
    NULL,
    NULL,
    NULL
    WHERE NOT EXISTS (
    SELECT 1 FROM site_settings
);

INSERT INTO contact_methods (
    created_at,
    updated_at,
    type,
    label_pt,
    label_en,
    value,
    icon_name,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    'PHONE',
    'Telefone principal',
    'Main phone',
    '+244 944 053 343',
    'phone',
    TRUE,
    1
    WHERE NOT EXISTS (
    SELECT 1 FROM contact_methods
    WHERE type = 'PHONE'
      AND value = '+244 944 053 343'
);

INSERT INTO contact_methods (
    created_at,
    updated_at,
    type,
    label_pt,
    label_en,
    value,
    icon_name,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    'WHATSAPP',
    'WhatsApp',
    'WhatsApp',
    '+244 944 053 343',
    'message-circle',
    TRUE,
    2
    WHERE NOT EXISTS (
    SELECT 1 FROM contact_methods
    WHERE type = 'WHATSAPP'
      AND value = '+244 944 053 343'
);

INSERT INTO sections (
    created_at,
    updated_at,
    slug,
    name_pt,
    name_en,
    description_pt,
    description_en,
    section_type,
    cover_image_url,
    cover_video_url,
    display_variant,
    layout_style,
    show_intro,
    show_gallery,
    show_filters,
    show_item_details,
    details_view_mode,
    allow_custom_attributes,
    settings_json,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    'materials',
    'Materiais',
    'Materials',
    'Materiais e produtos de publicidade, impressão, sinalização e acabamento extraídos do catálogo enviado.',
    'Advertising, printing, signage, and finishing materials extracted from the provided catalog.',
    'DIRECT_ITEMS',
    NULL,
    NULL,
    NULL,
    NULL,
    TRUE,
    TRUE,
    FALSE,
    TRUE,
    NULL,
    TRUE,
    NULL,
    TRUE,
    1
    WHERE NOT EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Chapas de Alucubond',
    'Alucubond Sheets',
    'Produto de elevada qualidade, resistente e com aparência única.',
    'A high-quality, resistant product with a unique appearance.',
    'Produto de elevada qualidade, resistente e com aparência única, ALUCOBOND é sinónimo de qualidade de construção sustentável e com os mais altos padrões criativos. Alucubond é produzido em várias espessuras de núcleo num processo contínuo de laminação e cortado ao formato.',
    'A high-quality, durable product with a unique appearance, ALUCOBOND is synonymous with sustainable construction quality and high creative standards. Alucubond is produced with several core thicknesses in a continuous lamination process and cut to size.',
    NULL,
    NULL,
    'PRODUCT',
    'Espessuras de núcleo variadas.',
    'Various core thicknesses.',
    NULL,
    TRUE,
    TRUE,
    1
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Alucubond Sheets'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Tela de Canvas',
    'Canvas Print',
    'Quadros diferenciados com aspecto artístico e impressão de altíssima qualidade.',
    'Distinctive artistic frames with very high-quality printing.',
    'Tela canvas são quadros diferenciados com aspecto artístico, com uma impressão de altíssima qualidade aplicada em trama de tecido com aparência rústica. Nossos canvas são feitos com tecidos 100% algodão, animoto e resinado para sua decoração. Temos tela abstrata.',
    'Canvas prints are distinctive frames with an artistic look, featuring very high-quality printing applied to woven fabric with a rustic appearance. Our canvases are made from 100% cotton, treated and resin-coated fabric for decoration. Abstract canvas options are available.',
    NULL,
    NULL,
    'PRODUCT',
    'Tecido 100% algodão; acabamento resinado.',
    '100% cotton fabric; resin-coated finish.',
    NULL,
    FALSE,
    TRUE,
    2
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Canvas Print'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Mastro da Bandeira',
    'Flag Pole / Flag Display',
    'Bandeiras e mastros para representação e publicidade de qualquer país.',
    'Flags and display poles for representation and advertising of any country.',
    'As bandeiras representam simbolicamente a soberania de uma nação. E nós os representamos nas publicidades dos países, porque fizemos bandeiras de qualquer país.',
    'Flags symbolically represent the sovereignty of a nation. We reproduce them for promotional purposes and create flags for any country.',
    NULL,
    NULL,
    'PRODUCT',
    'Disponível para diferentes países e formatos promocionais.',
    'Available for different countries and promotional formats.',
    NULL,
    FALSE,
    TRUE,
    3
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Flag Pole / Flag Display'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Roll Up',
    'Roll-Up Banner',
    'Suporte popular em feiras e eventos, impresso em lona opaca.',
    'A popular stand for fairs and events, printed on opaque canvas.',
    'O Roll UP é dos suportes mais populares em feiras e eventos. O designer publicitário é impresso em lona opaca. Mais rápido e fácil de montar. Traz como acessórios um saco de transporte simples.',
    'The roll-up is one of the most popular displays for fairs and events. The advertising design is printed on opaque canvas. It is quick and easy to assemble and comes with a simple carrying bag.',
    NULL,
    NULL,
    'PRODUCT',
    'Inclui saco de transporte simples.',
    'Includes a simple carrying bag.',
    NULL,
    TRUE,
    TRUE,
    4
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Roll-Up Banner'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Kit de Tintas Eco Solvente para Plotter de Impressão XP 600',
    'Eco-Solvent Ink Kit for XP 600 Printing Plotter',
    'Tinta solvente com materiais menos tóxicos na composição.',
    'Solvent ink with less toxic materials in its composition.',
    'A tinta Eco Solvente é um tipo de tinta solvente e, por isso, atua sob o mesmo princípio. No entanto apresenta uma grande diferença: os materiais menos tóxicos na sua composição, como é o caso do glicol, álcool ou estrois.',
    'Eco-solvent ink is a type of solvent ink and therefore works on the same principle. However, it differs by using less toxic materials in its composition, such as glycol, alcohol, or esters.',
    NULL,
    NULL,
    'PRODUCT',
    'Compatível com plotter XP 600.',
    'Compatible with XP 600 plotters.',
    NULL,
    FALSE,
    TRUE,
    5
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Eco-Solvent Ink Kit for XP 600 Printing Plotter'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Rolos de Vinil Perfurado',
    'Perforated Vinyl Rolls',
    'Material vinílico diferenciado, durável e resistente, com propriedade reflectora.',
    'A distinct vinyl material, durable and resistant, with reflective properties.',
    'Vinil perfurado é um material extremamente diferenciado do vinil normal ou qualquer outro material vinílico, com aparência sensível, mas ampla durável e resistente, propriedade reflectora capaz de mesmo no escuro ser visível intensamente com cor branca.',
    'Perforated vinyl is a highly distinctive material compared with standard vinyl or any other vinyl material. It offers a sensitive appearance while remaining durable and resistant, with reflective properties that keep it highly visible in white even in the dark.',
    NULL,
    NULL,
    'PRODUCT',
    'Temos: 1,07 cm, 1,27 cm e 1,52 cm.',
    'Available sizes: 1.07 cm, 1.27 cm, and 1.52 cm.',
    NULL,
    FALSE,
    TRUE,
    6
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Perforated Vinyl Rolls'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Rolos de Vinil Normal',
    'Standard Vinyl Rolls',
    'Material adesivo com maior resistência e flexibilidade.',
    'Adhesive material with greater resistance and flexibility.',
    'Vinil normal, matéria adesivo, possui maior resistência e flexibilidade pois é feito de um tipo de material plástico. Disponível em diversas cores e textura.',
    'Standard vinyl is an adhesive material with higher resistance and flexibility because it is made from a type of plastic material. It is available in several colors and textures.',
    NULL,
    NULL,
    'PRODUCT',
    'Temos: 1,07 cm, 1,27 cm e 1,52 cm.',
    'Available sizes: 1.07 cm, 1.27 cm, and 1.52 cm.',
    NULL,
    FALSE,
    TRUE,
    7
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Standard Vinyl Rolls'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Rolos de Lona',
    'Canvas Rolls',
    'Material versátil de ampla aplicação com alta resistência e durabilidade.',
    'A versatile material with broad application, high resistance, and durability.',
    'A lona é um material versátil, de ampla aplicação, garantindo ótimos resultados com diversas cores na impressão, brilho e beleza inigualável, altamente resistente, durabilidade excepcional mesmo exposta à intempéries. Possui duração estimada em 10 anos segundo o fabricante.',
    'Canvas is a versatile material with broad application, delivering excellent printing results with various colors, brightness, and unmatched beauty. It is highly resistant and has exceptional durability even when exposed to weather. Its estimated lifespan is 10 years according to the manufacturer.',
    NULL,
    NULL,
    'PRODUCT',
    'Temos: 100 m; 1,20 cm; 1,60 cm; 1,80 cm; 2,60 cm; 2,80 cm; 3,20 cm.',
    'Available sizes: 100 m, 1.20 cm, 1.60 cm, 1.80 cm, 2.60 cm, 2.80 cm, and 3.20 cm.',
    NULL,
    TRUE,
    TRUE,
    8
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Canvas Rolls'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Chapas Acrílica a Cores',
    'Colored Acrylic Sheets',
    'Material versátil, resistente e com diversas cores.',
    'A versatile, resistant material available in several colors.',
    'O acrílico é um material versátil, de ampla aplicação, garantindo ótimos resultados com diversas cores, brilho e beleza inigualável. Altamente resistente, com durabilidade excepcional mesmo exposta à intempéries, não desbota e possui duração estimada em 10 anos segundo o fabricante.',
    'Acrylic is a versatile material with broad application, delivering excellent results in various colors with shine and unmatched beauty. It is highly resistant, has exceptional durability even when exposed to weather, does not fade, and has an estimated lifespan of 10 years according to the manufacturer.',
    NULL,
    NULL,
    'PRODUCT',
    'Espessura disponível: 3 mm.',
    'Available thickness: 3 mm.',
    NULL,
    FALSE,
    TRUE,
    9
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Colored Acrylic Sheets'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Chapas Acrílica Transparente',
    'Transparent Acrylic Sheets',
    'Acrílico transparente com várias espessuras.',
    'Transparent acrylic available in multiple thicknesses.',
    'O acrílico é um material versátil, de ampla aplicação, garantindo ótimos resultados com diversas cores, brilho e beleza inigualável. Altamente resistente, durabilidade excepcional mesmo exposta à intempéries, não desbota e possui duração estimada em 10 anos segundo o fabricante.',
    'Acrylic is a versatile material with broad application, delivering excellent results with shine and unmatched beauty. It is highly resistant, offers exceptional durability even when exposed to weather, does not fade, and has an estimated lifespan of 10 years according to the manufacturer.',
    NULL,
    NULL,
    'PRODUCT',
    'Espessuras: 2 mm, 3 mm, 6 mm, 10 mm.',
    'Thicknesses: 2 mm, 3 mm, 6 mm, 10 mm.',
    NULL,
    FALSE,
    TRUE,
    10
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'Transparent Acrylic Sheets'
);

INSERT INTO section_items (
    created_at,
    updated_at,
    section_id,
    category_id,
    title_pt,
    title_en,
    short_description_pt,
    short_description_en,
    full_description_pt,
    full_description_en,
    cover_image_url,
    video_url,
    item_type,
    specifications_pt,
    specifications_en,
    attributes_json,
    is_featured,
    is_active,
    sort_order
)
SELECT
    NOW(),
    NOW(),
    (SELECT id FROM sections WHERE slug = 'materials'),
    NULL,
    'Placas PVC',
    'PVC Boards',
    'Plásticos muito usados em comércios, indústrias e construção civil.',
    'Widely used plastic boards for commerce, industry, and civil construction.',
    'As placas PVC são plásticos mais usados por ter uma ampla variedade de aplicações em comércios, indústrias, construção civil, decoração, entre outros. Elas podem ser facilmente gravadas, serigrafadas, impressas, laminadas, pregadas e serradas.',
    'PVC boards are among the most widely used plastics because of their broad range of applications in commerce, industry, civil construction, decoration, and more. They can be easily engraved, screen-printed, printed, laminated, nailed, and sawn.',
    NULL,
    NULL,
    'PRODUCT',
    'Espessuras: 3 mm, 5 mm, 8 mm, 10 mm e 15 mm.',
    'Thicknesses: 3 mm, 5 mm, 8 mm, 10 mm, and 15 mm.',
    NULL,
    FALSE,
    TRUE,
    11
    WHERE EXISTS (
    SELECT 1 FROM sections WHERE slug = 'materials'
)
AND NOT EXISTS (
    SELECT 1
    FROM section_items si
    JOIN sections s ON s.id = si.section_id
    WHERE s.slug = 'materials'
      AND si.title_en = 'PVC Boards'
);

COMMIT;