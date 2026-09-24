package com.kroste.twitchchatbackend.fishing;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FishCatalog {

    private final List<FishSpecies> species = List.of(
            //Common
            new FishSpecies("minnow", "Пескарь", Rarity.COMMON, 10, 5000),
            new FishSpecies("gudgeon", "Пескарь речной", Rarity.COMMON, 10, 5000),
            new FishSpecies("old_boot", "Старый дырявый сапог", Rarity.COMMON, 10, 5000),
            new FishSpecies("plastic_bag", "Пакет с пакетами", Rarity.COMMON, 10, 5000),
            new FishSpecies("aluminium_can", "Банка Балтики 9", Rarity.COMMON, 10, 5000),
            new FishSpecies("candy_wrapper", "Фантик от конфеты", Rarity.COMMON, 10, 5000),
            new FishSpecies("carp", "Карп", Rarity.COMMON, 15, 4500),
            new FishSpecies("perch", "Окунь", Rarity.COMMON, 12, 4800),
            new FishSpecies("roach", "Плотва", Rarity.COMMON, 11, 4900),
            new FishSpecies("crucian", "Карась", Rarity.COMMON, 13, 4700),
            new FishSpecies("rudd", "Красноперка", Rarity.COMMON, 12, 4600),

            //Uncommon
            new FishSpecies("pike", "Щука", Rarity.UNCOMMON, 30, 3400),
            new FishSpecies("trout", "Форель", Rarity.UNCOMMON, 35, 3200),
            new FishSpecies("bream", "Лещ", Rarity.UNCOMMON, 28, 3500),
            new FishSpecies("zander", "Судак", Rarity.UNCOMMON, 32, 3300),
            new FishSpecies("salmon", "Лосось", Rarity.UNCOMMON, 38, 3000),
            new FishSpecies("asp", "Жерех", Rarity.UNCOMMON, 29, 3400),
            new FishSpecies("nokia_3310", "Nokia 3310 (рабочая)", Rarity.UNCOMMON, 40, 3000),
            new FishSpecies("fidget_spinner", "Спиннер из 2017 года", Rarity.UNCOMMON, 34, 3500),
            new FishSpecies("lost_keys", "Ключи от квартиры", Rarity.UNCOMMON, 38, 3100),
            new FishSpecies("memes_flashdrive", "Флешка с мемами", Rarity.UNCOMMON, 36, 3300),
            new FishSpecies("chips", "Пачка чипсов", Rarity.UNCOMMON, 33, 3300),

            //Rare
            new FishSpecies("beluga_bany", "Малёк белуги", Rarity.RARE, 75, 1850),
            new FishSpecies("golden_carp", "Золотой карп", Rarity.RARE, 80, 1800),
            new FishSpecies("sterlet", "Стерлядь", Rarity.RARE, 85, 1800),
            new FishSpecies("catfish", "Сом", Rarity.RARE, 90, 1700),
            new FishSpecies("eel", "Речной угорь", Rarity.RARE, 95, 1700),
            new FishSpecies("gta6_disk", "Диск с GTA 6", Rarity.RARE, 80, 1600),
            new FishSpecies("bitcoin_physical", "Физическая монета Биткоина", Rarity.RARE, 99, 1500),
            new FishSpecies("dino_chicken_nugget", "Наггетс в форме динозавра", Rarity.RARE, 110, 1400),
            new FishSpecies("pubg_underwear", "Трусы PUBG", Rarity.RARE, 110, 1400),
            new FishSpecies("chat_diploma", "Диплом «Почтённый чаттер»", Rarity.RARE, 97, 1400),
            new FishSpecies("krabsburger", "Крабсбурегер", Rarity.RARE, 100, 1400),

            //Epic
            new FishSpecies("sturgeon", "Осётр", Rarity.EPIC, 200, 700),
            new FishSpecies("electric_eel", "Угорь", Rarity.EPIC, 220, 700),
            new FishSpecies("beluga", "Белуга", Rarity.EPIC, 240, 700),
            new FishSpecies("arapaima", "Арапайма", Rarity.EPIC, 210, 700),
            new FishSpecies("axolotl", "Аксолотль", Rarity.EPIC, 260, 650),
            new FishSpecies("shrek_swamp_dirt", "Грязь с болота Шрека", Rarity.EPIC, 250, 650),
            new FishSpecies("pudge_hook", "Хук Пуджа", Rarity.EPIC, 300, 500),
            new FishSpecies("anime_waifu_pillow", "Дакимакура со Спанч Бобом", Rarity.EPIC, 350, 500),
            new FishSpecies("ikea_shark", "Акула из Икеи", Rarity.EPIC, 370, 500),
            new FishSpecies("doomguy", "Фигурка Думгая", Rarity.EPIC, 330, 550),
            new FishSpecies("sign_bikinibottom", "Табличка «Бикини Боттом»", Rarity.EPIC, 350, 650),

            //Legendary
            new FishSpecies("rainbow_koi", "Радужный кои", Rarity.LEGENDARY, 500, 350),
            new FishSpecies("leviathan", "Левиафан", Rarity.LEGENDARY, 550, 350),
            new FishSpecies("kraken_spawn", "Маленький Кракен", Rarity.LEGENDARY, 520, 350),
            new FishSpecies("golden_koi", "Золотой кои", Rarity.LEGENDARY, 600, 300),
            new FishSpecies("cthulhu_eye", "Око Ктулху", Rarity.LEGENDARY, 580, 300),
            new FishSpecies("joel", "Joel", Rarity.LEGENDARY, 800, 250),
            new FishSpecies("dodo_slice", "Кусок додопиццы", Rarity.LEGENDARY, 900, 200),

            //Mythic
            new FishSpecies("cthulhu", "Ктулху", Rarity.MYTHIC, 1600, 100),
            new FishSpecies("poseidon", "Посейдон", Rarity.MYTHIC, 1500, 100),
            new FishSpecies("recrent_glasses", "Очки Рекрента", Rarity.MYTHIC, 2000, 100),
            new FishSpecies("megalodon", "Мегалодон", Rarity.MYTHIC, 2000, 100),
            new FishSpecies("jormungand", "Йормунганд", Rarity.MYTHIC, 2200, 100)
    );

    private final List<Mutation> mutations = List.of(
            new Mutation("shiny", "Shiny", 0.05, 2.0),
            new Mutation("giant", "Giant", 0.08, 1.5),
            new Mutation("albino", "Albino", 0.06, 1.8),
            new Mutation("two_headed", "Two-Headed", 0.06, 1.7),
            new Mutation("ghost", "Ghost", 0.04, 2.2),
            new Mutation("toxic", "Toxic", 0.03, 3.0),
            new Mutation("golden", "Golden", 0.02, 3.5)
    );

    public List<FishSpecies> species() {
        return species;
    }

    public List<Mutation> mutations() {
        return mutations;
    }

    public int totalSpeciesWeight() {
        return species.stream().mapToInt(FishSpecies::weight).sum();
    }

    public Optional<FishSpecies> findSpeciesById(String id) {
        return species.stream().filter(s -> s.id().equals(id)).findFirst();
    }

    public Optional<Mutation> findMutationById(String id) {
        return mutations.stream().filter(m -> m.id().equals(id)).findFirst();
    }
}
